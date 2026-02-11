package com.base.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.exception.BusinessException;
import com.base.common.util.HttpClientUtil;
import com.base.config.OauthConfig;
import com.base.system.dto.LoginResponse;
import com.base.system.dto.oauth.*;
import com.base.system.entity.SysUser;
import com.base.system.entity.UserOauth;
import com.base.system.entity.UserRole;
import com.base.system.mapper.SysUserMapper;
import com.base.system.mapper.UserOauthMapper;
import com.base.system.mapper.UserRoleMapper;
import com.base.system.service.OauthService;
import com.base.system.util.JwtUtil;
import com.base.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * OAuth 第三方登录服务实现类
 */
@Slf4j
@Service
public class OauthServiceImpl implements OauthService {

    /**
     * OAuth state 前缀
     */
    private static final String OAUTH_STATE_PREFIX = "oauth:state:";

    /**
     * OAuth 绑定临时凭证前缀
     */
    private static final String OAUTH_BIND_TOKEN_PREFIX = "oauth:bindToken:";

    /**
     * Token 存储前缀（与 AuthServiceImpl 保持一致）
     */
    private static final String TOKEN_PREFIX = "token:";

    /**
     * GitHub 授权地址
     */
    private static final String GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize";

    /**
     * GitHub Token 交换地址
     */
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

    /**
     * GitHub 用户信息地址
     */
    private static final String GITHUB_USER_URL = "https://api.github.com/user";

    /**
     * OAuth 平台类型：GitHub
     */
    private static final String OAUTH_TYPE_GITHUB = "github";

    /**
     * state 过期时间（秒）
     */
    private static final long STATE_EXPIRE_SECONDS = 180;

    /**
     * 绑定凭证过期时间（秒）
     */
    private static final long BIND_TOKEN_EXPIRE_SECONDS = 300;

    /**
     * OAuth 新用户默认角色ID
     */
    @Value("${oauth.default-role-id:2}")
    private Long defaultRoleId;

    /**
     * OAuth 是否启用
     */
    @Value("${oauth.enabled:false}")
    private Boolean oauthEnabled;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private OauthConfig oauthConfig;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserOauthMapper userOauthMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isOauthEnabled() {
        return Boolean.TRUE.equals(oauthEnabled);
    }

    @Override
    public String getGithubAuthUrl() {
        checkOauthEnabled();
        // 生成随机 state 防止 CSRF
        String state = UUID.randomUUID().toString().replace("-", "");
        // 存入 Redis，3分钟过期
        redisUtil.set(OAUTH_STATE_PREFIX + state, "1", STATE_EXPIRE_SECONDS);

        // 拼接 GitHub 授权 URL
        String url = GITHUB_AUTHORIZE_URL
                + "?client_id=" + oauthConfig.getClientId()
                + "&redirect_uri=" + oauthConfig.getRedirectUri()
                + "&scope=read:user user:email"
                + "&state=" + state;

        log.info("生成 GitHub 授权 URL，state: {}", state);
        return url;
    }

    @Override
    public OauthCallbackResponse handleGithubCallback(OauthCallbackRequest request) {
        checkOauthEnabled();
        // 1. 校验 state
        String stateKey = OAUTH_STATE_PREFIX + request.getState();
        Object stateValue = redisUtil.get(stateKey);
        if (stateValue == null) {
            throw new BusinessException("无效的 state 参数或已过期");
        }
        // 用后删除，防止重放
        redisUtil.delete(stateKey);

        // 2. 用 code 换 access_token
        String accessToken = exchangeAccessToken(request.getCode());

        // 3. 获取 GitHub 用户信息
        GithubUserInfo githubUser = fetchGithubUserInfo(accessToken);
        githubUser.setAccessToken(accessToken);
        githubUser.setOauthType(OAUTH_TYPE_GITHUB);

        // 4. 查询绑定关系
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, OAUTH_TYPE_GITHUB)
                .eq(UserOauth::getOauthId, githubUser.getId());
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);

        OauthCallbackResponse response = new OauthCallbackResponse();

        if (userOauth != null) {
            // 已绑定，直接登录
            SysUser user = sysUserMapper.selectById(userOauth.getUserId());
            if (user == null) {
                throw new BusinessException("绑定的用户不存在");
            }
            if (user.getStatus() != null && user.getStatus() == 0) {
                throw new BusinessException("账号已被禁用");
            }

            // 更新 OAuth 信息
            userOauth.setOauthName(githubUser.getLogin());
            userOauth.setOauthAvatar(githubUser.getAvatarUrl());
            userOauth.setOauthEmail(githubUser.getEmail());
            userOauth.setAccessToken(accessToken);
            userOauthMapper.updateById(userOauth);

            // 生成 JWT
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            redisUtil.set(TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

            response.setToken(token);
            response.setExpiresIn(jwtExpiration);
            response.setNeedBind(false);

            log.info("GitHub OAuth 登录成功，userId: {}, githubId: {}", user.getId(), githubUser.getId());
        } else {
            // 未绑定，生成临时凭证
            String oauthToken = UUID.randomUUID().toString().replace("-", "");
            redisUtil.set(OAUTH_BIND_TOKEN_PREFIX + oauthToken, githubUser, BIND_TOKEN_EXPIRE_SECONDS);

            response.setOauthToken(oauthToken);
            response.setNeedBind(true);
            response.setOauthName(githubUser.getLogin());
            response.setOauthAvatar(githubUser.getAvatarUrl());

            log.info("GitHub OAuth 需要绑定，githubId: {}, githubName: {}", githubUser.getId(), githubUser.getLogin());
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse bindNewUser(OauthBindNewRequest request) {
        checkOauthEnabled();
        // 1. 从 Redis 获取 GitHub 用户信息
        GithubUserInfo githubUser = getGithubUserFromRedis(request.getOauthToken());

        // 2. 生成用户名，确保唯一
        String username = "github_" + githubUser.getId();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该 GitHub 账号已被绑定");
        }

        // 3. 创建新用户
        SysUser newUser = new SysUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setNickname(githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin());
        newUser.setAvatar(githubUser.getAvatarUrl());
        newUser.setEmail(githubUser.getEmail());
        newUser.setStatus(1);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(newUser);

        // 4. 分配默认角色
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(defaultRoleId);
        userRole.setCreateTime(LocalDateTime.now());
        userRoleMapper.insert(userRole);

        // 5. 创建绑定记录
        saveUserOauth(newUser.getId(), githubUser);

        // 6. 删除临时凭证
        redisUtil.delete(OAUTH_BIND_TOKEN_PREFIX + request.getOauthToken());

        // 7. 生成 JWT
        String token = jwtUtil.generateToken(newUser.getId(), newUser.getUsername());
        redisUtil.set(TOKEN_PREFIX + newUser.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        log.info("GitHub OAuth 创建新账号并绑定成功，userId: {}, username: {}", newUser.getId(), username);

        return new LoginResponse(token, jwtExpiration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse bindExistUser(OauthBindExistRequest request) {
        checkOauthEnabled();
        // 1. 从 Redis 获取 GitHub 用户信息
        GithubUserInfo githubUser = getGithubUserFromRedis(request.getOauthToken());

        // 2. 验证用户名和密码
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 3. 检查该用户是否已绑定同类型的第三方账号
        LambdaQueryWrapper<UserOauth> oauthWrapper = new LambdaQueryWrapper<>();
        oauthWrapper.eq(UserOauth::getUserId, user.getId())
                .eq(UserOauth::getOauthType, githubUser.getOauthType());
        if (userOauthMapper.selectCount(oauthWrapper) > 0) {
            throw new BusinessException("该账号已绑定了 GitHub 账号，请先解绑");
        }

        // 4. 创建绑定记录
        saveUserOauth(user.getId(), githubUser);

        // 5. 删除临时凭证
        redisUtil.delete(OAUTH_BIND_TOKEN_PREFIX + request.getOauthToken());

        // 6. 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        redisUtil.set(TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.MILLISECONDS);

        log.info("GitHub OAuth 绑定已有账号成功，userId: {}, githubId: {}", user.getId(), githubUser.getId());

        return new LoginResponse(token, jwtExpiration);
    }

    @Override
    public List<UserOauth> listUserOauthBindings(Long userId) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId);
        return userOauthMapper.selectList(wrapper);
    }

    @Override
    public void unbind(Long userId, String oauthType) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, oauthType);
        int deleted = userOauthMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException("未找到对应的绑定记录");
        }
        log.info("解绑第三方账号成功，userId: {}, oauthType: {}", userId, oauthType);
    }

    /**
     * 检查 OAuth 是否启用
     */
    private void checkOauthEnabled() {
        if (!Boolean.TRUE.equals(oauthEnabled)) {
            throw new BusinessException("第三方登录功能未启用");
        }
    }

    /**
     * 用 code 换取 GitHub access_token
     *
     * @param code 授权码
     * @return access_token
     */
    private String exchangeAccessToken(String code) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("client_id", oauthConfig.getClientId());
        params.put("client_secret", oauthConfig.getClientSecret());
        params.put("code", code);

        Map<String, String> headers = new HashMap<>(2);
        headers.put("Accept", "application/json");

        String responseStr = HttpClientUtil.post(GITHUB_TOKEN_URL, params, headers);
        JSONObject jsonObject = JSON.parseObject(responseStr);

        String accessToken = jsonObject.getString("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            String error = jsonObject.getString("error_description");
            log.error("GitHub access_token 获取失败: {}", error);
            throw new BusinessException("GitHub 授权失败: " + (error != null ? error : "未知错误"));
        }

        return accessToken;
    }

    /**
     * 获取 GitHub 用户信息  *
     * @param accessToken access_token
     * @return GitHub 用户信息
     */
    private GithubUserInfo fetchGithubUserInfo(String accessToken) {
        Map<String, String> headers = new HashMap<>(2);
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Accept", "application/json");

        String responseStr = HttpClientUtil.get(GITHUB_USER_URL, headers);
        JSONObject jsonObject = JSON.parseObject(responseStr);

        GithubUserInfo userInfo = new GithubUserInfo();
        userInfo.setId(String.valueOf(jsonObject.get("id")));
        userInfo.setLogin(jsonObject.getString("login"));
        userInfo.setName(jsonObject.getString("name"));
        userInfo.setAvatarUrl(jsonObject.getString("avatar_url"));
        userInfo.setEmail(jsonObject.getString("email"));

        return userInfo;
    }

    /**
     * 从 Redis 获取临时存储的 GitHub 用户信息
     *
     * @param oauthToken 临时绑定凭证
     * @return GitHub 用户信息
     */
    private GithubUserInfo getGithubUserFromRedis(String oauthToken) {
        String key = OAUTH_BIND_TOKEN_PREFIX + oauthToken;
        Object cached = redisUtil.get(key);
        if (cached == null) {
            throw new BusinessException("绑定凭证已过期，请重新授权");
        }
        // Redis 反序列化可能返回 LinkedHashMap，需要转换
        if (cached instanceof GithubUserInfo) {
            return (GithubUserInfo) cached;
        }
        return JSON.parseObject(JSON.toJSONString(cached), GithubUserInfo.class);
    }

    /**
     * 保存第三方绑定记录
     *
     * @param userId     用户ID
     * @param githubUser GitHub 用户信息
     */
    private void saveUserOauth(Long userId, GithubUserInfo githubUser) {
        UserOauth userOauth = new UserOauth();
        userOauth.setUserId(userId);
        userOauth.setOauthType(githubUser.getOauthType());
        userOauth.setOauthId(githubUser.getId());
        userOauth.setOauthName(githubUser.getLogin());
        userOauth.setOauthAvatar(githubUser.getAvatarUrl());
        userOauth.setOauthEmail(githubUser.getEmail());
        userOauth.setAccessToken(githubUser.getAccessToken());
        userOauthMapper.insert(userOauth);
    }
}
