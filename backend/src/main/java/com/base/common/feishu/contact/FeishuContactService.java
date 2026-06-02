package com.base.common.feishu.contact;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.feishu.client.FeishuApiClient;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.contact.ThirdPartyContactService;
import com.base.common.thirdparty.contact.ThirdPartyUser;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.UserOauthMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 飞书通讯录同步服务
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuContactService implements ThirdPartyContactService {

    private final FeishuApiClient feishuApiClient;
    private final UserOauthMapper userOauthMapper;

    @Override
    public ThirdPartyPlatform getPlatform() {
        return ThirdPartyPlatform.FEISHU;
    }

    @Override
    public int syncAllUsers() {
        int totalSynced = 0;
        String pageToken = null;

        do {
            Map<String, Object> params = new HashMap<>(8);
            params.put("page_size", 50);
            params.put("user_id_type", "open_id");
            if (pageToken != null) {
                params.put("page_token", pageToken);
            }

            JSONObject result = feishuApiClient.get("/contact/v3/users", params);
            JSONObject data = result.getJSONObject("data");
            if (data == null) {
                break;
            }

            JSONArray items = data.getJSONArray("items");
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    upsertUserOauth(item);
                    totalSynced++;
                }
            }

            Boolean hasMore = data.getBoolean("has_more");
            pageToken = Boolean.TRUE.equals(hasMore) ? data.getString("page_token") : null;
        } while (pageToken != null);

        log.info("飞书通讯录同步完成，共同步 {} 个用户", totalSynced);
        return totalSynced;
    }

    @Override
    public ThirdPartyUser getUserByOpenId(String openId) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("user_id_type", "open_id");

        JSONObject result = feishuApiClient.get("/contact/v3/users/" + openId, params);
        JSONObject data = result.getJSONObject("data");
        if (data == null) {
            return null;
        }
        JSONObject user = data.getJSONObject("user");
        if (user == null) {
            return null;
        }
        return mapToThirdPartyUser(user);
    }

    private void upsertUserOauth(JSONObject feishuUser) {
        String openId = feishuUser.getString("open_id");
        String name = feishuUser.getString("name");
        String avatar = feishuUser.getString("avatar_url");
        String email = feishuUser.getString("email");

        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "feishu")
                .eq(UserOauth::getOauthId, openId);
        UserOauth existing = userOauthMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setOauthName(name);
            existing.setOauthAvatar(avatar);
            existing.setOauthEmail(email);
            existing.setUpdateTime(LocalDateTime.now());
            userOauthMapper.updateById(existing);
        }
    }

    private ThirdPartyUser mapToThirdPartyUser(JSONObject user) {
        ThirdPartyUser thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setOpenId(user.getString("open_id"));
        thirdPartyUser.setUnionId(user.getString("union_id"));
        thirdPartyUser.setName(user.getString("name"));
        thirdPartyUser.setEmail(user.getString("email"));
        thirdPartyUser.setMobile(user.getString("mobile"));
        thirdPartyUser.setAvatar(user.getString("avatar_url"));
        JSONArray deptIds = user.getJSONArray("department_ids");
        if (deptIds != null && !deptIds.isEmpty()) {
            thirdPartyUser.setDepartmentId(deptIds.getString(0));
        }
        return thirdPartyUser;
    }
}
