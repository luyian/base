package com.base.system.service.impl;

import com.base.common.service.CosService;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.util.HttpClientUtil;
import com.base.system.dto.CaptchaResponse;
import com.base.system.dto.LoginRequest;
import com.base.system.dto.LoginResponse;
import com.base.system.dto.RegisterRequest;
import com.base.system.dto.RouterVO;
import com.base.system.dto.UserInfoResponse;
import com.base.system.dto.WxBindRequest;
import com.base.system.dto.WxLoginRequest;
import com.base.system.entity.Dept;
import com.base.system.entity.Permission;
import com.base.system.entity.Role;
import com.base.system.entity.SysUser;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.DeptMapper;
import com.base.system.mapper.PermissionMapper;
import com.base.system.mapper.RoleMapper;
import com.base.system.mapper.SysUserMapper;
import com.base.system.mapper.UserOauthMapper;
import com.base.system.mapper.UserRoleMapper;
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
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
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

    @Autowired
    private UserOauthMapper userOauthMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private CosService cosService;

    /**
     * 微信小程序是否启用
     */
    @Autowired
    private com.base.config.WechatOauthProperties wechatOauthProperties;

    /**
     * 默认角色ID
     */
    @Value("${oauth.default-role-id:2}")
    private Long defaultRoleId;
    @Value("${oauth.mini-service-role-id:4}")
    private Long miniServiceRoleId;

    /**
     * 验证码是否启用
     */
    @Value("${captcha.enabled:true}")
    private Boolean captchaEnabled;

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
        // 验证码未启用时，返回空数据并标记未启用
        if (!Boolean.TRUE.equals(captchaEnabled)) {
            CaptchaResponse response = new CaptchaResponse();
            response.setEnabled(false);
            log.info("验证码未启用，跳过生成");
            return response;
        }

        // 生成验证码
        String code = CaptchaUtil.generateCode();
        String captchaKey = UUID.randomUUID().toString();

        // 生成验证码图片
        String captchaImage = CaptchaUtil.generateImageBase64(code);

        // 存储验证码到 Redis（不区分大小写）
        redisUtil.set(CAPTCHA_PREFIX + captchaKey, code.toLowerCase(), captchaExpiration, TimeUnit.SECONDS);

        log.info("生成验证码成功，captchaKey: {}", captchaKey);

        CaptchaResponse response = new CaptchaResponse(captchaKey, captchaImage, captchaExpiration, true);
        return response;
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

        // 2. 验证验证码（未启用时跳过）
        if (Boolean.TRUE.equals(captchaEnabled)) {
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
        }

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
    public LoginResponse wxLogin(WxLoginRequest request) {
        // 1. 检查微信登录是否启用
        if (!Boolean.TRUE.equals(wechatOauthProperties.getEnabled())) {
            throw new BusinessException(500, "微信登录未启用");
        }

        // 2. 调用微信API获取openid/unionid（按 appId 路由对应 secret）
        WxSession session = getWechatSession(request.getCode(), request.getAppId());
        if (session == null || session.getOpenid() == null) {
            throw new BusinessException("微信登录失败：无效的code");
        }
        String openid = session.getOpenid();

        // 3. 查询是否已绑定用户
        UserOauth userOauth = findWechatOauthByOpenid(openid);

        // 3.1 跨小程序：openid 未命中时按 unionid 兜底（同一微信开放平台下 unionid 唯一）。
        //     命中说明该微信用户已在其他小程序绑定过 → 自动补录当前小程序的 openid，下次直接命中。
        if (userOauth == null && StringUtils.hasText(session.getUnionid())) {
            userOauth = findWechatOauthByUnionid(session.getUnionid());
            if (userOauth != null) {
                addWechatOauth(userOauth.getUserId(), openid, session.getUnionid(), request.getAppId());
                log.info("微信跨小程序自动补录 openid，userId: {}, appId: {}", userOauth.getUserId(), request.getAppId());
            }
        } else if (userOauth != null && !StringUtils.hasText(userOauth.getUnionId())
                && StringUtils.hasText(session.getUnionid())) {
            // 3.2 openid 命中但缺 unionid：顺手补齐，便于其他小程序识别
            userOauth.setUnionId(session.getUnionid());
            userOauthMapper.updateById(userOauth);
        }

        SysUser user;
        if (userOauth == null) {
            // 4.1 新用户：返回特定状态码，让小程序跳转到绑定页面
            // 这里我们抛出一个特殊异常，前端可以根据这个判断需要绑定
            throw new BusinessException(401, "NEED_BIND");
        } else {
            // 4.2 老用户：查询绑定账号
            user = userMapper.selectById(userOauth.getUserId());
            if (user == null) {
                // 账号已删除，抛出需要绑定的错误
                throw new BusinessException(401, "NEED_BIND");
            }
        }

        // 5. 检查用户状态
        if (user.getStatus() == 0) {
            saveLoginLog(user.getUsername(), 0, "账号已被禁用");
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 6. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 7. 存储 Token 到 Redis
        redisUtil.set(TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        // 8. 记录登录日志
        saveLoginLog(user.getUsername(), 1, "微信登录成功");
        log.info("微信用户登录成功，openid: {}, userId: {}", openid, user.getId());

        return new LoginResponse(token, jwtExpiration);
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        
        userMapper.insert(user);

        // 3. 分配默认角色
        if (defaultRoleId != null) {
            com.base.system.entity.UserRole userRole = new com.base.system.entity.UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(defaultRoleId);
            userRoleMapper.insert(userRole);
        }

        log.info("用户注册成功，username: {}", request.getUsername());

        // 4. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        redisUtil.set(TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        return new LoginResponse(token, jwtExpiration);
    }

    @Override
    public LoginResponse bindWechat(WxBindRequest request) {
        // 1. 调用微信API获取openid/unionid
        WxSession session = getWechatSession(request.getCode(), request.getAppId());
        if (session == null || session.getOpenid() == null) {
            throw new BusinessException("微信绑定失败：无效的code");
        }
        String openid = session.getOpenid();

        // 2. 检查微信是否已被绑定
        LambdaQueryWrapper<UserOauth> oauthWrapper = new LambdaQueryWrapper<>();
        oauthWrapper.eq(UserOauth::getOauthType, "wechat");
        oauthWrapper.eq(UserOauth::getOauthId, openid);
        UserOauth existOauth = userOauthMapper.selectOne(oauthWrapper);
        if (existOauth != null) {
            throw new BusinessException("该微信已被绑定");
        }

        SysUser user;
        
        // 3. 如果提供了用户名和密码，先验证账号密码登录
        if (StringUtils.hasText(request.getUsername()) && StringUtils.hasText(request.getPassword())) {
            // 账号密码登录验证
            LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(SysUser::getUsername, request.getUsername());
            user = userMapper.selectOne(userWrapper);
            
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException("密码错误");
            }
            
            if (user.getStatus() == 0) {
                throw new BusinessException("账号已被禁用");
            }
        } else {
            // 没有提供账号密码，创建新用户
            String username = "wx_" + openid.substring(0, 16);
            user = new SysUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setNickname("微信用户");
            user.setStatus(1);
            userMapper.insert(user);
            
            // 分配小程序角色
            Long roleId = miniServiceRoleId != null ? miniServiceRoleId : 4L;
            if (defaultRoleId != null) {
                com.base.system.entity.UserRole userRole = new com.base.system.entity.UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        // 4. 绑定微信
        UserOauth oauth = new UserOauth();
        oauth.setUserId(user.getId());
        oauth.setOauthType("wechat");
        oauth.setOauthId(openid);
        oauth.setUnionId(session.getUnionid());
        oauth.setAppId(request.getAppId());
        oauth.setCreateTime(java.time.LocalDateTime.now());
        userOauthMapper.insert(oauth);
        // 同一微信用户可能已绑定其他小程序：补齐该用户所有微信记录的 unionid
        fillUnionIdForUser(user.getId(), session.getUnionid());

        log.info("微信绑定成功，userId: {}, openid: {}, unionid: {}", user.getId(), openid, session.getUnionid());

        // 5. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        redisUtil.set(TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        return new LoginResponse(token, jwtExpiration);
    }

    @Override
    public void bindWechatForCurrentUser(String code, String appId) {
        // 从SecurityUtils获取当前登录用户
        Long userId = SecurityUtils.getCurrentUserId();

        // 调用微信API获取openid/unionid
        WxSession session = getWechatSession(code, appId);
        if (session == null || session.getOpenid() == null) {
            throw new BusinessException("微信绑定失败：无效的code");
        }
        String openid = session.getOpenid();

        // 检查微信是否已被绑定
        LambdaQueryWrapper<UserOauth> oauthWrapper = new LambdaQueryWrapper<>();
        oauthWrapper.eq(UserOauth::getOauthType, "wechat");
        oauthWrapper.eq(UserOauth::getOauthId, openid);
        UserOauth existOauth = userOauthMapper.selectOne(oauthWrapper);
        if (existOauth != null) {
            if (existOauth.getUserId().equals(userId)) {
                // 已经是当前用户绑定的（顺带补齐 unionid）
                if (!StringUtils.hasText(existOauth.getUnionId()) && StringUtils.hasText(session.getUnionid())) {
                    existOauth.setUnionId(session.getUnionid());
                    userOauthMapper.updateById(existOauth);
                    fillUnionIdForUser(userId, session.getUnionid());
                }
                return;
            }
            throw new BusinessException("该微信已被其他账号绑定");
        }

        // 支持多小程序各自绑定：同一用户可持有多条微信记录（每个小程序的 openid 一条，
        // 唯一键 oauth_type+oauth_id 保证 openid 不冲突）。同一 openid 的幂等/冲突已在上方处理。
        addWechatOauth(userId, openid, session.getUnionid(), appId);
        // 同一微信用户可能已绑定其他小程序：补齐该用户所有微信记录的 unionid
        fillUnionIdForUser(userId, session.getUnionid());

        log.info("用户 {} 绑定微信成功，openid: {}, unionid: {}", userId, openid, session.getUnionid());
    }

    @Override
    public void unbindWechatForCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询当前用户的全部微信绑定记录
        List<UserOauth> existing = listWechatOauthByUserId(userId);

        if (existing.isEmpty()) {
            throw new BusinessException("您未绑定微信");
        }

        // 删除该用户全部微信绑定记录（跨小程序的所有 openid 一并解绑）
        existing.forEach(o -> userOauthMapper.deleteById(o.getId()));

        log.info("用户 {} 解绑微信成功，共删除 {} 条绑定记录", userId, existing.size());
    }

    /**
     * 调用微信API获取openid与unionid
     * <p>支持多小程序：优先按 appId 从 {@link WechatOauthProperties#getAppSecrets()} 路由对应 secret，未命中时回退默认 appId/appSecret。</p>
     * <p>unionid 仅在小程序绑定到微信开放平台账号时返回，用于跨小程序识别同一用户。</p>
     *
     * @param code  微信登录code
     * @param appId 发起登录的小程序 appId（可为空）
     * @return 包含 openid/unionid 的会话信息，失败返回 null
     */
    private WxSession getWechatSession(String code, String appId) {
        try {
            String resolvedAppId = wechatOauthProperties.getAppId();
            String resolvedSecret = wechatOauthProperties.getAppSecret();
            Map<String, String> appSecrets = wechatOauthProperties.getAppSecrets();
            if (appId != null && appSecrets != null && appSecrets.containsKey(appId)) {
                resolvedAppId = appId;
                resolvedSecret = appSecrets.get(appId);
            }
            String url = "https://api.weixin.qq.com/sns/jscode2session" +
                    "?appid=" + resolvedAppId +
                    "&secret=" + resolvedSecret +
                    "&js_code=" + code +
                    "&grant_type=authorization_code";

            String response = HttpClientUtil.get(url, null);
            JSONObject json = JSON.parseObject(response);

            if (json.containsKey("openid")) {
                WxSession session = new WxSession();
                session.setOpenid(json.getString("openid"));
                session.setUnionid(json.getString("unionid"));
                // 诊断日志：确认 unionid 是否返回（unionid 为空 = 两小程序未绑定同一开放平台）
                log.info("微信 jscode2session 成功，openid: {}, unionid: {}", session.getOpenid(), session.getUnionid());
                return session;
            }

            log.error("微信登录失败：{}", response);
            return null;
        } catch (Exception e) {
            log.error("调用微信API失败", e);
            return null;
        }
    }

    /**
     * 按 openid 查询微信绑定记录
     */
    private UserOauth findWechatOauthByOpenid(String openid) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "wechat");
        wrapper.eq(UserOauth::getOauthId, openid);
        return userOauthMapper.selectOne(wrapper);
    }

    /**
     * 按 unionid 查询微信绑定记录（跨小程序识别同一用户；unionid 理论上唯一，limit 1 兜底）
     */
    private UserOauth findWechatOauthByUnionid(String unionid) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "wechat");
        wrapper.eq(UserOauth::getUnionId, unionid);
        wrapper.orderByDesc(UserOauth::getId);
        wrapper.last("limit 1");
        return userOauthMapper.selectOne(wrapper);
    }

    /**
     * 新增一条微信绑定记录（openid + unionid + appId）
     */
    private void addWechatOauth(Long userId, String openid, String unionid, String appId) {
        UserOauth oauth = new UserOauth();
        oauth.setUserId(userId);
        oauth.setOauthType("wechat");
        oauth.setOauthId(openid);
        oauth.setUnionId(unionid);
        oauth.setAppId(appId);
        oauth.setCreateTime(java.time.LocalDateTime.now());
        userOauthMapper.insert(oauth);
    }

    /**
     * 查询指定用户的全部微信绑定记录（按 id 升序，先绑定的在前）
     */
    private List<UserOauth> listWechatOauthByUserId(Long userId) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "wechat");
        wrapper.eq(UserOauth::getUserId, userId);
        wrapper.orderByAsc(UserOauth::getId);
        return userOauthMapper.selectList(wrapper);
    }

    /**
     * 补齐指定用户所有微信绑定记录的 unionid（同一微信用户在多个小程序的 openid 共享同一 unionid）
     */
    private void fillUnionIdForUser(Long userId, String unionid) {
        if (!StringUtils.hasText(unionid)) {
            return;
        }
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "wechat");
        wrapper.eq(UserOauth::getUserId, userId);
        wrapper.and(w -> w.isNull(UserOauth::getUnionId).or().eq(UserOauth::getUnionId, ""));
        java.util.List<UserOauth> list = userOauthMapper.selectList(wrapper);
        for (UserOauth o : list) {
            o.setUnionId(unionid);
            userOauthMapper.updateById(o);
        }
    }

    /**
     * 微信 jscode2session 会话结果
     */
    private static class WxSession {
        private String openid;
        private String unionid;

        String getOpenid() {
            return openid;
        }

        void setOpenid(String openid) {
            this.openid = openid;
        }

        String getUnionid() {
            return unionid;
        }

        void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }

    /**
     * 创建微信用户
     */
    private SysUser createWechatUser(String openid, WxLoginRequest request) {
        // 生成唯一用户名
        String username = "wx_" + openid.substring(0, 16);

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setNickname(request.getNickname() != null ? request.getNickname() : "微信用户");
        user.setAvatar(request.getAvatarUrl());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setStatus(1);
        
        // 分配小程序用户角色
        Long roleId = miniServiceRoleId != null ? miniServiceRoleId : 4L;

        userMapper.insert(user);

        // 分配默认角色
        if (defaultRoleId != null) {
            com.base.system.entity.UserRole userRole = new com.base.system.entity.UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }

        // 绑定第三方登录
        UserOauth oauth = new UserOauth();
        oauth.setUserId(user.getId());
        oauth.setOauthType("wechat");
        oauth.setOauthId(openid);
        oauth.setOauthName(request.getNickname());
        oauth.setOauthAvatar(request.getAvatarUrl());
        oauth.setCreateTime(java.time.LocalDateTime.now());
        userOauthMapper.insert(oauth);

        log.info("创建微信用户成功，username: {}, openid: {}", username, openid);

        return user;
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
    public UserInfoResponse getUserInfo(String appId) {
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
        String avatar = user.getAvatar();
        if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
            response.setAvatar(cosService.getFileUrl(avatar));
        } else {
            response.setAvatar(avatar);
        }
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

        // 查询微信绑定状态：传入 appId 时仅反映「当前小程序」的绑定（历史记录 app_id 为空，
        // 不会误匹配），不传则保持旧行为（任意一条绑定即视为已绑定，兼容 web 端）
        LambdaQueryWrapper<UserOauth> oauthWrapper = new LambdaQueryWrapper<>();
        oauthWrapper.eq(UserOauth::getOauthType, "wechat");
        oauthWrapper.eq(UserOauth::getUserId, user.getId());
        if (StringUtils.hasText(appId)) {
            oauthWrapper.eq(UserOauth::getAppId, appId);
        }
        UserOauth userOauth = userOauthMapper.selectList(oauthWrapper).stream()
                .findFirst()
                .orElse(null);
        if (userOauth != null) {
            response.setWxOpenid(userOauth.getOauthId());
        }

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
