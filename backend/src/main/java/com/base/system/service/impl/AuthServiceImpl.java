package com.base.system.service.impl;

import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.CaptchaResponse;
import com.base.system.dto.LoginRequest;
import com.base.system.dto.LoginResponse;
import com.base.system.dto.RouterVO;
import com.base.system.dto.UserInfoResponse;
import com.base.system.entity.Dept;
import com.base.system.entity.Permission;
import com.base.system.entity.Role;
import com.base.system.entity.SysUser;
import com.base.system.mapper.DeptMapper;
import com.base.system.mapper.PermissionMapper;
import com.base.system.mapper.RoleMapper;
import com.base.system.mapper.SysUserMapper;
import com.base.system.service.AuthService;
import com.base.system.service.LoginLogService;
import com.base.system.entity.LoginLog;
import com.base.system.util.IpUtils;
import com.base.system.util.JwtUtil;
import com.base.system.util.RedisUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import com.base.util.CaptchaUtil;
import com.base.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 验证码过期时间（秒）
     */
    @Value("${captcha.expiration:300}")
    private Long captchaExpiration;

    /**
     * 登录失败最大次数
     */
    @Value("${login.max-retry:5}")
    private Integer maxRetry;

    /**
     * 登录失败锁定时间（分钟）
     */
    @Value("${login.lock-time:30}")
    private Integer lockTime;

    /**
     * JWT 过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final String LOGIN_RETRY_PREFIX = "login:retry:";
    private static final String TOKEN_PREFIX = "token:";

    @Override
    public CaptchaResponse generateCaptcha() {
        // 生成验证码
        String code = CaptchaUtil.generateCode();
        String captchaKey = UUID.randomUUID().toString();

        // 生成验证码图片
        String captchaImage = CaptchaUtil.generateImageBase64(code);

        // 存储验证码到 Redis（不区分大小写）
        redisUtil.set(CAPTCHA_PREFIX + captchaKey, code.toLowerCase(), captchaExpiration, TimeUnit.SECONDS);

        log.info("生成验证码成功，captchaKey: {}", captchaKey);

        return new CaptchaResponse(captchaKey, captchaImage, captchaExpiration);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();

        // 1. 检查账号是否被锁定
        String retryKey = LOGIN_RETRY_PREFIX + username;
        Integer retryCount = redisUtil.get(retryKey, Integer.class);
        if (retryCount != null && retryCount >= maxRetry) {
            saveLoginLog(username, 0, "账号已锁定");
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
        }

        // 2. 验证验证码
        String captchaKey = CAPTCHA_PREFIX + request.getCaptchaKey();
        String cachedCaptcha = redisUtil.get(captchaKey, String.class);
        if (cachedCaptcha == null) {
            throw new BusinessException(ResultCode.CAPTCHA_EXPIRED);
        }
        if (!cachedCaptcha.equalsIgnoreCase(request.getCaptcha())) {
            throw new BusinessException(ResultCode.CAPTCHA_ERROR);
        }
        // 验证码使用后立即删除
        redisUtil.delete(captchaKey);

        // 3. 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            // 记录失败次数
            recordLoginFailure(username);
            saveLoginLog(username, 0, "用户名或密码错误");
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 记录失败次数
            recordLoginFailure(username);
            saveLoginLog(username, 0, "用户名或密码错误");
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 5. 检查用户状态
        if (user.getStatus() == 0) {
            saveLoginLog(username, 0, "账号已被禁用");
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 6. 清除登录失败记录
        redisUtil.delete(retryKey);

        // 7. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 8. 存储 Token 到 Redis
        redisUtil.set(TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        // 9. 记录登录日志
        saveLoginLog(username, 1, "登录成功");
        log.info("用户登录成功，username: {}, userId: {}", username, user.getId());

        return new LoginResponse(token, jwtExpiration);
    }

    @Override
    public void logout() {
        // 从 SecurityContext 获取当前用户名
        String username = SecurityUtils.getCurrentUsername();
        if (username != null) {
            // 查询用户ID
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, username);
            SysUser user = userMapper.selectOne(wrapper);

            if (user != null) {
                // 删除 Redis 中的 Token
                redisUtil.delete(TOKEN_PREFIX + user.getId());
                log.info("用户登出成功，username: {}, userId: {}", username, user.getId());
            }
        } else {
            log.warn("用户登出失败，未找到当前登录用户");
        }
    }

    @Override
    public String refreshToken(String token) {
        // 刷新 Token
        String newToken = jwtUtil.refreshToken(token);
        if (newToken == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            // 更新 Redis 中的 Token
            redisUtil.set(TOKEN_PREFIX + userId, newToken, jwtExpiration, TimeUnit.MILLISECONDS);
        }

        log.info("Token 刷新成功，userId: {}", userId);

        return newToken;
    }

    @Override
    public UserInfoResponse getUserInfo() {
        // 获取当前用户名
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 查询用户信息
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        response.setAvatar(user.getAvatar());
        response.setDeptId(user.getDeptId());

        // 查询部门名称
        if (user.getDeptId() != null) {
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                response.setDeptName(dept.getDeptName());
            }
        }

        // 查询用户角色
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        response.setRoles(roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList()));

        // 查询用户权限
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
        response.setPermissions(permissions.stream()
                .map(Permission::getPermissionCode)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList()));

        log.info("获取用户信息成功，username: {}", username);

        return response;
    }

    @Override
    public List<RouterVO> getRouters() {
        // 获取当前用户名
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 查询用户信息
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 查询用户权限菜单
        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());

        // 过滤出菜单类型的权限（type=1目录，type=2菜单）
        List<Permission> menuPermissions = permissions.stream()
                .filter(p -> p.getType() == 1 || p.getType() == 2)
                .filter(p -> p.getVisible() == 1)
                .filter(p -> p.getStatus() == 1)
                .sorted(Comparator.comparing(Permission::getSort))
                .collect(Collectors.toList());

        // 构建树形结构
        List<RouterVO> routers = buildRouterTree(menuPermissions, 0L);

        log.info("获取用户路由菜单成功，username: {}, 菜单数量: {}", username, routers.size());

        return routers;
    }

    /**
     * 构建路由树
     *
     * @param permissions 权限列表  * @param parentId    父级ID
     * @return 路由树
     */
    private List<RouterVO> buildRouterTree(List<Permission> permissions, Long parentId) {
        List<RouterVO> routers = new ArrayList<>();

        for (Permission permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                RouterVO router = new RouterVO();
                router.setId(permission.getId());
                router.setParentId(permission.getParentId());
                router.setName(permission.getPermissionName());
                router.setPath(permission.getPath());
                router.setComponent(permission.getComponent());

                // 设置元信息
                RouterVO.RouterMeta meta = new RouterVO.RouterMeta();
                meta.setTitle(permission.getPermissionName());
                meta.setIcon(permission.getIcon());
                meta.setHidden(permission.getVisible() == 0);
                meta.setPermission(permission.getPermissionCode());
                router.setMeta(meta);

                // 递归查找子路由
                List<RouterVO> children = buildRouterTree(permissions, permission.getId());
                if (!children.isEmpty()) {
                    router.setChildren(children);
                }

                routers.add(router);
            }
        }

        return routers;
    }

    /**
     * 记录登录失败次数
     *
     * @param username 用户名
     */
    private void recordLoginFailure(String username) {
        String retryKey = LOGIN_RETRY_PREFIX + username;
        Integer retryCount = redisUtil.get(retryKey, Integer.class);

        if (retryCount == null) {
            retryCount = 0;
        }

        retryCount++;
        redisUtil.set(retryKey, retryCount, lockTime, TimeUnit.MINUTES);

        int remainingRetries = maxRetry - retryCount;
        if (remainingRetries > 0) {
            log.warn("用户登录失败，username: {}, 剩余重试次数: {}", username, remainingRetries);
        } else {
            log.warn("用户登录失败次数过多，账号已锁定，username: {}, 锁定时间: {} 分钟", username, lockTime);
        }
    }

    /**
     * 记录登录日志
     *
     * @param username 用户名
     * @param status   登录状态（0-失败 1-成功）
     * @param message  提示信息
     */
    private void saveLoginLog(String username, Integer status, String message) {
        try {
            LoginLog loginLog = new LoginLog();
            loginLog.setUsername(username);
            loginLog.setStatus(status);
            loginLog.setMessage(message);

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                loginLog.setLoginIp(IpUtils.getIpAddress(request));

                // 解析 User-Agent 获取浏览器和操作系统信息
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null) {
                    loginLog.setBrowser(parseBrowser(userAgent));
                    loginLog.setOs(parseOs(userAgent));
                }
            }

            // 设置登录地点（简化处理，内网IP显示"内网IP"）
            String ip = loginLog.getLoginIp();
            if (ip != null && IpUtils.isInternalIp(ip)) {
                loginLog.setLoginLocation("内网IP");
            } else {
                loginLog.setLoginLocation("未知");
            }

            loginLogService.saveLoginLog(loginLog);
        } catch (Exception e) {
            log.error("保存登录日志失败", e);
        }
    }

    /**
     * 解析浏览器信息
     *
     * @param userAgent User-Agent 字符串
     * @return 浏览器名称
     */
    private String parseBrowser(String userAgent) {
        if (userAgent == null) {
            return "未知";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edg")) {
            return "Edge";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("safari")) {
            return "Safari";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            return "Opera";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "IE";
        }
        return "未知";
    }

    /**
     * 解析操作系统信息
     *
     * @param userAgent User-Agent 字符串
     * @return 操作系统名称
     */
    private String parseOs(String userAgent) {
        if (userAgent == null) {
            return "未知";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "Mac OS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        }
        return "未知";
    }
}
