package com.base.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.ai.config.AiChatModelHolder;
import com.base.ai.config.AiSkillConfig;
import com.base.ai.dto.ChatRequest;
import com.base.ai.dto.ChatResponse;
import com.base.ai.service.AiConfigProvider;
import com.base.ai.service.AiService;
import com.base.ai.skill.GaokaoDataTools;
import com.base.ai.skill.StockDataTools;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 对话服务实现（基于 LangChain4j ChatLanguageModel）
 * 支持普通对话和带技能的对话（Function Calling）
 *
 * @author base
 * @since 2026-03-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private static final Pattern SCORE_PATTERN = Pattern.compile("(\\d{3})\\s*分");

    private static final Pattern LOOSE_SCORE_PATTERN = Pattern.compile("(?<!\\d)([1-6]\\d{2}|7[0-4]\\d|750)(?!\\d)");

    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d{1,3})\\s*(条|个|所)");

    private static final Pattern RANK_PATTERN = Pattern.compile("(位次|排名)\\s*[:：]?\\s*(\\d+(?:\\.\\d+)?)(万)?");

    private static final Pattern YEAR_PATTERN = Pattern.compile("(20\\d{2})\\s*年");

    private static final Pattern TUITION_PATTERN = Pattern.compile("(学费|费用)[^\\d]*(\\d+(?:\\.\\d+)?)(万|千|元)?");

    private final AiConfigProvider aiConfigProvider;
    private final AiChatModelHolder chatModelHolder;
    private final AiSkillConfig skillConfig;
    private final StockDataTools stockDataTools;
    private final GaokaoDataTools gaokaoDataTools;

    @Override
    public ChatResponse chat(ChatRequest request) {
        int maxMsg = aiConfigProvider.getMaxMessageLength() != null ? aiConfigProvider.getMaxMessageLength() : 2000;
        int maxCtx = aiConfigProvider.getMaxContextLength() != null ? aiConfigProvider.getMaxContextLength() : 5000;
        String userMessage = validateUserMessage(request.getMessage(), maxMsg);
        request.setMessage(userMessage);
        if (request.getContext() != null && request.getContext().length() > maxCtx) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "上下文长度不能超过 " + maxCtx + " 字符");
        }
        if (isGaokaoRecommendMessage(userMessage)) {
            return chatWithGaokaoRecommendation(userMessage);
        }

        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        ChatLanguageModel model = chatModelHolder.getModel(aiConfigProvider);
        if (model == null) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }

        List<ChatMessage> messages = buildMessages(request);
        long start = System.currentTimeMillis();
        try {
            Response<AiMessage> response = model.generate(messages);
            long cost = System.currentTimeMillis() - start;
            String text = response != null && response.content() != null ? response.content().text() : null;
            String answer = text != null ? text : "";
            log.info("AI 对话成功，耗时 {} ms", cost);
            return new ChatResponse(answer);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("AI 对话失败，耗时 {} ms，原因: {}", cost, e.getMessage());
            if (shouldFallbackToGaokaoRecommendation(request.getMessage(), e)) {
                return chatWithGaokaoRecommendation(request.getMessage());
            }
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "AI 服务暂时不可用: " + (e.getMessage() != null ? e.getMessage() : "请求失败"));
        }
    }

    @Override
    public ChatResponse chatWithSkills(ChatRequest request) {
        int maxMsg = aiConfigProvider.getMaxMessageLength() != null ? aiConfigProvider.getMaxMessageLength() : 2000;
        String userMessage = validateUserMessage(request.getMessage(), maxMsg);
        request.setMessage(userMessage);
        if (isGaokaoRecommendMessage(userMessage)) {
            return chatWithGaokaoRecommendation(userMessage);
        }

        if (!aiConfigProvider.isConfigured()) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }
        if (!Boolean.TRUE.equals(skillConfig.getEnabled())) {
            // 技能未启用，走普通对话
            return chat(request);
        }

        ChatLanguageModel model = chatModelHolder.getModel(aiConfigProvider);
        if (model == null) {
            throw new BusinessException(ResultCode.AI_NOT_CONFIGURED);
        }

        long start = System.currentTimeMillis();
        try {
            // 构建带 Tools 的 AI 代理
            SkillAssistant assistant = AiServices.builder(SkillAssistant.class)
                    .chatLanguageModel(model)
                    .tools(stockDataTools, gaokaoDataTools)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build();

            // 构造提问（含系统提示和上下文）
            String fullMessage = buildSkillMessage(request);
            String answer = assistant.chat(fullMessage);

            long cost = System.currentTimeMillis() - start;
            log.info("AI 技能对话成功，耗时 {} ms", cost);
            return new ChatResponse(answer != null ? answer : "");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("AI 技能对话失败，耗时 {} ms，原因: {}", cost, e.getMessage());
            if (shouldFallbackToGaokaoRecommendation(userMessage, e)) {
                return chatWithGaokaoRecommendation(userMessage);
            }
            // 降级到普通对话
            log.info("技能对话失败，降级到普通对话模式");
            try {
                return chat(request);
            } catch (Exception fallbackEx) {
                throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                        "AI 服务暂时不可用: " + (e.getMessage() != null ? e.getMessage() : "请求失败"));
            }
        }
    }

    /**
     * LangChain4j AiServices 代理接口
     */
    interface SkillAssistant {
        @dev.langchain4j.service.SystemMessage("你是一个专业的数据分析助手，可以处理 A 股数据分析和高考志愿报考推荐。"
                + "回答必须使用中文，数据展示要清晰、有条理。"
                + "当用户询问股票、行情、资金流向、龙虎榜、北向资金、行业排名等问题时，主动调用股票工具获取数据。"
                + "当用户询问高考、志愿、报考、学校、专业推荐，尤其出现“理科400分”“推荐10所”等描述时，主动调用高考推荐工具。"
                + "高考推荐默认使用河南考生（candidateProvinceCode=410000）和2025年数据，除非用户明确指定省份或年份。"
                + "用户未指定推荐数量时默认推荐10所学校；指定数量时最多推荐50所学校。"
                + "如果用户提供位次、专业偏好、城市或地区偏好、公办/民办、学费上限等条件，必须传入高考推荐工具对应参数。"
                + "没有位次时可以让工具基于历史近分录取记录粗略估算，但回答中要说明估算不等同于当年一分一段表。"
                + "用户指定“推荐N所/条/个”时，将 size 设置为 N；输出按可冲、较稳、保底分组，展示学校、专业、最低分、最低位次、招生计划、学费等关键信息。"
                + "如果工具返回 error 字段，请告知用户数据获取失败及可能原因。"
                + "重要提示：股票数据仅供参考，不构成投资建议；高考推荐基于历史录取数据和招生计划，仅供参考，实际填报请结合当年一分一段表、招生章程和个人偏好。")
        String chat(@dev.langchain4j.service.UserMessage String message);
    }

    private ChatResponse chatWithGaokaoRecommendation(String userMessage) {
        log.info("命中高考推荐本地工具链，不调用大模型接口，用户问题: {}", userMessage);
        String score = extractScore(userMessage);
        if (!StringUtils.hasText(score)) {
            return new ChatResponse("请补充高考分数，例如：理科400分，推荐10所学校和专业。"
                    + "如果有位次、城市、专业偏好、公办/民办、学费上限，也可以一起说明。");
        }

        String majorPreference = extractMajorPreference(userMessage);
        String majorName = extractMajorNameFilter(userMessage, majorPreference);
        String toolResult = gaokaoDataTools.recommendGaokaoSchoolMajors(
                extractSubjectCategory(userMessage),
                score,
                extractRank(userMessage),
                extractProvince(userMessage),
                extractAdmissionYear(userMessage),
                extractBatchName(userMessage),
                majorName,
                majorPreference,
                extractCityPreference(userMessage),
                extractCollegeNature(userMessage),
                extractMaxTuitionFee(userMessage),
                extractSelectionRequirement(userMessage),
                extractRecommendSize(userMessage));
        return new ChatResponse(formatGaokaoRecommendationAnswer(userMessage, toolResult));
    }

    private String formatGaokaoRecommendationAnswer(String userMessage, String toolResult) {
        try {
            JSONObject result = JSON.parseObject(toolResult);
            if (result == null) {
                return "未获取到有效的高考推荐结果，请稍后重试。";
            }
            if (result.containsKey("error")) {
                return "推荐失败：" + result.getString("error");
            }
            JSONArray recommendations = result.getJSONArray("recommendations");
            if (recommendations == null || recommendations.isEmpty()) {
                return "暂未匹配到符合条件的学校和专业。建议放宽批次、专业、地区或学费条件后再试。";
            }

            StringBuilder sb = new StringBuilder();
            JSONObject query = result.getJSONObject("query");
            appendGaokaoSummary(sb, userMessage, result, recommendations);
            appendGaokaoAnalysis(sb, result, query, recommendations);
            sb.append("\n### 推荐清单\n");
            int[] index = {1};
            appendRecommendationBucket(sb, recommendations, "可冲", index, query);
            appendRecommendationBucket(sb, recommendations, "较稳", index, query);
            appendRecommendationBucket(sb, recommendations, "保底", index, query);
            appendRecommendationBucket(sb, recommendations, "补充参考", index, query);
            String notice = result.getString("notice");
            if (StringUtils.hasText(notice)) {
                sb.append("\n### 填报提醒\n").append(notice);
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("格式化高考推荐结果失败: {}", e.getMessage());
            return "已完成高考推荐查询，但结果格式化失败。原始结果：" + toolResult;
        }
    }

    private void appendGaokaoAnalysis(StringBuilder sb, JSONObject result, JSONObject query,
                                      JSONArray recommendations) {
        JSONObject rank = result.getJSONObject("rankConversion");
        sb.append("\n### 分析依据\n");
        sb.append("- 数据：专业录取分数线优先，招生计划补充计划数、学制和学费。\n");
        if (rank != null) {
            sb.append("- 位次：").append(readString(rank, "description", "未获取到位次说明")).append("\n");
        }
        String rule = result.getString("recommendRule");
        if (StringUtils.hasText(rule)) {
            sb.append("- 分档：").append(rule).append("\n");
        }
        sb.append("- 排序：综合分由档位、分数差、位次差、偏好、性质、学费、计划数和 985/211 加权得到。\n");
        sb.append("- 规模：最终输出 ").append(recommendations.size()).append(" 所学校");
        String preferenceSummary = buildPreferenceSummary(query);
        if (StringUtils.hasText(preferenceSummary)) {
            sb.append("，已纳入").append(preferenceSummary);
        }
        sb.append("。\n");
    }

    private void appendGaokaoSummary(StringBuilder sb, String userMessage, JSONObject result,
                                     JSONArray recommendations) {
        JSONObject query = result.getJSONObject("query");
        JSONObject rank = result.getJSONObject("rankConversion");
        sb.append("### 推荐概况\n");
        sb.append("- 原问题：").append(userMessage).append("\n");
        if (query != null) {
            sb.append("- 条件：").append(readString(query, "candidateProvinceName", "河南"))
                    .append("，").append(readString(query, "admissionYear", "2025")).append("年");
            appendIfPresent(sb, "，科类", readString(query, "subjectCategory", null));
            appendMatchedSubjectCategory(sb, query, recommendations);
            appendIfPresent(sb, "，分数", readString(query, "score", null));
            appendIfPresent(sb, "，批次", readString(query, "batchName", null));
            sb.append("\n");
        }
        if (rank != null && rank.get("rank") != null) {
            sb.append("- 位次：").append(rank.get("rank"))
                    .append("（").append(readString(rank, "source", "未知来源"))
                    .append("，可信度：").append(readString(rank, "confidence", "未知")).append("）\n");
        }
    }

    private void appendRecommendationBucket(StringBuilder sb, JSONArray recommendations,
                                            String bucket, int[] index, JSONObject query) {
        int bucketCount = countBucket(recommendations, bucket);
        if (bucketCount == 0) {
            return;
        }
        sb.append("\n#### ").append(bucket).append("（").append(bucketCount).append(" 所）\n");
        sb.append("| 序号 | 学校 | 专业 | 最低分 | 位次 | 性质 | 理由 |\n");
        sb.append("| --- | --- | --- | --- | --- | --- | --- |\n");
        int bucketIndex = 1;
        for (Object object : recommendations) {
            if (!(object instanceof JSONObject)) {
                continue;
            }
            JSONObject item = (JSONObject) object;
            if (!bucket.equals(item.getString("bucket"))) {
                continue;
            }
            appendRecommendationLine(sb, item, index[0]++, bucketIndex++, query);
        }
    }

    private void appendRecommendationLine(StringBuilder sb, JSONObject item, int index, int bucketIndex,
                                          JSONObject query) {
        sb.append("| ").append(index).append(" / ").append(bucketIndex)
                .append(" | ").append(tableCell(readString(item, "collegeName", "-")))
                .append(" | ").append(tableCell(readString(item, "majorName", "-")))
                .append(" | ").append(tableCell(buildScoreText(item)))
                .append(" | ").append(tableCell(readString(item, "minRank", "-")))
                .append(" | ").append(tableCell(readString(item, "collegeNature", "-")))
                .append(" | ").append(tableCell(buildRecommendationReason(item, query)))
                .append(" |\n");
    }

    private void appendIfPresent(StringBuilder sb, String label, String value) {
        if (StringUtils.hasText(value)) {
            sb.append(label).append(value);
        }
    }

    private void appendMatchedSubjectCategory(StringBuilder sb, JSONObject query, JSONArray recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return;
        }
        JSONObject firstItem = null;
        for (Object object : recommendations) {
            if (object instanceof JSONObject) {
                firstItem = (JSONObject) object;
                break;
            }
        }
        if (firstItem == null) {
            return;
        }
        String querySubject = readString(query, "subjectCategory", null);
        String matchedSubject = readString(firstItem, "subjectCategory", null);
        if (StringUtils.hasText(matchedSubject) && !matchedSubject.equals(querySubject)) {
            sb.append("（按").append(matchedSubject).append("数据匹配）");
        }
    }

    private int countBucket(JSONArray recommendations, String bucket) {
        int count = 0;
        for (Object object : recommendations) {
            if (object instanceof JSONObject && bucket.equals(((JSONObject) object).getString("bucket"))) {
                count++;
            }
        }
        return count;
    }

    private String buildPreferenceSummary(JSONObject query) {
        if (query == null) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        appendJsonArrayPreference(parts, query, "majorPreference", "专业偏好");
        appendJsonArrayPreference(parts, query, "cityPreference", "地区偏好");
        appendJsonArrayPreference(parts, query, "collegeNature", "院校性质");
        String maxTuitionFee = readString(query, "maxTuitionFee", null);
        if (StringUtils.hasText(maxTuitionFee)) {
            parts.add("学费上限 " + maxTuitionFee + " 元/年");
        }
        return parts.isEmpty() ? null : String.join("、", parts);
    }

    private void appendJsonArrayPreference(List<String> parts, JSONObject query, String key, String label) {
        JSONArray values = query.getJSONArray(key);
        if (values == null || values.isEmpty()) {
            return;
        }
        List<String> texts = new ArrayList<>();
        for (Object value : values) {
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                texts.add(String.valueOf(value));
            }
        }
        if (!texts.isEmpty()) {
            parts.add(label + "：" + String.join("/", texts));
        }
    }

    private String buildRecommendationReason(JSONObject item, JSONObject query) {
        List<String> reasons = new ArrayList<>();
        appendReasonIfPresent(reasons, compactBucketAnalysis(item.getString("bucket")));
        appendReasonIfPresent(reasons, compactScoreDiff(readInteger(item, "scoreDiff")));
        appendReasonIfPresent(reasons, compactRankDiff(readInteger(item, "rankDiff")));
        appendPreferenceMatchReason(reasons, query, item, "majorPreference", "专业偏好");
        appendPreferenceMatchReason(reasons, query, item, "cityPreference", "地区偏好");
        appendPreferenceMatchReason(reasons, query, item, "collegeNature", "院校性质");
        appendTuitionReason(reasons, query, item);
        appendIfPresent(reasons, "计划数", readString(item, "planCount", null), "人");
        if (Integer.valueOf(1).equals(readInteger(item, "is985"))) {
            reasons.add("985 加分");
        } else if (Integer.valueOf(1).equals(readInteger(item, "is211"))) {
            reasons.add("211 加分");
        }
        return String.join("；", reasons);
    }

    private String buildScoreText(JSONObject item) {
        String minScore = readString(item, "minScore", "-");
        Integer scoreDiff = readInteger(item, "scoreDiff");
        if (scoreDiff == null) {
            return minScore;
        }
        return minScore + "(" + formatSigned(scoreDiff) + ")";
    }

    private String tableCell(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        return value.replace("\r", " ")
                .replace("\n", " ")
                .replace("|", "/")
                .trim();
    }

    private void appendReasonIfPresent(List<String> reasons, String reason) {
        if (StringUtils.hasText(reason)) {
            reasons.add(reason);
        }
    }

    private String compactBucketAnalysis(String bucket) {
        if ("可冲".equals(bucket)) {
            return "冲刺";
        }
        if ("较稳".equals(bucket)) {
            return "较稳";
        }
        if ("保底".equals(bucket)) {
            return "保底";
        }
        if ("补充参考".equals(bucket)) {
            return "补充";
        }
        return null;
    }

    private String compactScoreDiff(Integer scoreDiff) {
        if (scoreDiff == null) {
            return null;
        }
        if (scoreDiff > 0) {
            return "高" + scoreDiff + "分";
        }
        if (scoreDiff < 0) {
            return "低" + Math.abs(scoreDiff) + "分";
        }
        return "同分";
    }

    private String compactRankDiff(Integer rankDiff) {
        if (rankDiff == null) {
            return null;
        }
        if (rankDiff > 0) {
            return "位次后" + rankDiff;
        }
        if (rankDiff < 0) {
            return "位次前" + Math.abs(rankDiff);
        }
        return "位次相同";
    }

    private String bucketAnalysis(String bucket) {
        if ("可冲".equals(bucket)) {
            return "可冲档，历史线略高或位次要求更靠前，波动风险更高";
        }
        if ("较稳".equals(bucket)) {
            return "较稳档，历史线与当前分数/位次接近";
        }
        if ("保底".equals(bucket)) {
            return "保底档，历史线低于当前分数/位次";
        }
        if ("补充参考".equals(bucket)) {
            return "补充参考，候选不足时从扩大区间补入";
        }
        return null;
    }

    private String scoreDiffAnalysis(Integer scoreDiff) {
        if (scoreDiff == null) {
            return null;
        }
        if (scoreDiff > 0) {
            return "历史最低分高出考生 " + scoreDiff + " 分";
        }
        if (scoreDiff < 0) {
            return "历史最低分低于考生 " + Math.abs(scoreDiff) + " 分";
        }
        return "历史最低分与考生分数持平";
    }

    private String rankDiffAnalysis(Integer rankDiff) {
        if (rankDiff == null) {
            return null;
        }
        if (rankDiff > 0) {
            return "历史最低位次比估算位次靠后 " + rankDiff + " 名";
        }
        if (rankDiff < 0) {
            return "历史最低位次比估算位次靠前 " + Math.abs(rankDiff) + " 名";
        }
        return "历史最低位次与估算位次相同";
    }

    private void appendPreferenceMatchReason(List<String> reasons, JSONObject query, JSONObject item,
                                             String preferenceKey, String label) {
        if (!hasJsonArrayValues(query, preferenceKey)) {
            return;
        }
        String itemKey = preferenceKey + "Matched";
        Boolean matched = readBoolean(item, itemKey);
        if (Boolean.TRUE.equals(matched)) {
            reasons.add(label + "命中");
        } else {
            reasons.add(label + "未命中，主要靠分数/位次入选");
        }
    }

    private void appendTuitionReason(List<String> reasons, JSONObject query, JSONObject item) {
        if (query == null || query.get("maxTuitionFee") == null) {
            return;
        }
        Boolean matched = readBoolean(item, "tuitionFilterMatched");
        if (matched == null) {
            reasons.add("学费缺失，未按学费硬排除");
        } else if (Boolean.TRUE.equals(matched)) {
            reasons.add("学费在上限内");
        } else {
            reasons.add("学费超过上限，仅保留作参考");
        }
    }

    private boolean hasJsonArrayValues(JSONObject query, String key) {
        if (query == null) {
            return false;
        }
        JSONArray values = query.getJSONArray(key);
        return values != null && !values.isEmpty();
    }

    private void appendIfPresent(List<String> values, String label, String value, String suffix) {
        if (StringUtils.hasText(value)) {
            values.add(label + value + suffix);
        }
    }

    private String readString(JSONObject object, String key, String defaultValue) {
        Object value = object.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private Integer readInteger(JSONObject object, String key) {
        Object value = object.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean readBoolean(JSONObject object, String key) {
        Object value = object.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(String.valueOf(value));
    }

    private String formatSigned(Integer value) {
        if (value == null) {
            return "-";
        }
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private String buildSkillMessage(ChatRequest request) {
        StringBuilder sb = new StringBuilder();
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            sb.append("【当前上下文】\n").append(request.getContext()).append("\n\n");
        }
        sb.append(request.getMessage());
        return sb.toString();
    }

    private List<ChatMessage> buildMessages(ChatRequest request) {
        List<ChatMessage> messages = new ArrayList<>();
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            String systemContent = "以下为当前页面/系统提供的上下文，请结合上下文回答用户问题。\n\n" + request.getContext();
            messages.add(new SystemMessage(systemContent));
        }
        messages.add(new UserMessage(request.getMessage()));
        return messages;
    }

    private String validateUserMessage(String message, int maxMsg) {
        if (!StringUtils.hasText(message)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "问题不能为空");
        }
        String trimmed = message.trim();
        if (trimmed.length() > maxMsg) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "问题长度不能超过 " + maxMsg + " 字符");
        }
        return trimmed;
    }

    private boolean isGaokaoRecommendMessage(String message) {
        boolean hasScore = SCORE_PATTERN.matcher(message).find();
        if (!hasScore) {
            hasScore = LOOSE_SCORE_PATTERN.matcher(message).find();
        }
        boolean hasSubject = message.contains("理科") || message.contains("文科")
                || message.contains("物理类") || message.contains("历史类")
                || message.contains("物理") || message.contains("历史");
        boolean hasTarget = message.contains("学校") || message.contains("院校")
                || message.contains("大学") || message.contains("专业")
                || message.contains("志愿") || message.contains("录取");
        boolean hasGaokaoScene = message.contains("高考") || message.contains("志愿")
                || message.contains("报考") || message.contains("填报")
                || message.contains("冲稳保") || (hasScore && (hasSubject || hasTarget));
        boolean hasRecommendIntent = message.contains("推荐") || message.contains("能上")
                || message.contains("能报") || message.contains("能去") || message.contains("可报")
                || message.contains("可以上") || message.contains("报考") || message.contains("填报")
                || message.contains("选校") || message.contains("冲稳保");
        return hasGaokaoScene && hasRecommendIntent;
    }

    private String extractScore(String message) {
        Matcher matcher = SCORE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        matcher = LOOSE_SCORE_PATTERN.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }

    private boolean shouldFallbackToGaokaoRecommendation(String message, Exception exception) {
        if (!StringUtils.hasText(message) || !StringUtils.hasText(extractScore(message))) {
            return false;
        }
        log.warn("模型调用失败且问题符合高考推荐特征，回退本地推荐，异常: {}", exception.getMessage());
        return message.contains("理科") || message.contains("文科") || message.contains("物理")
                || message.contains("历史") || message.contains("高考") || message.contains("志愿")
                || message.contains("报考") || message.contains("学校") || message.contains("院校")
                || message.contains("大学") || message.contains("专业") || message.contains("推荐");
    }

    private String extractRecommendSize(String message) {
        Matcher matcher = SIZE_PATTERN.matcher(message);
        while (matcher.find()) {
            String value = matcher.group(1);
            if (!value.equals(extractScore(message))) {
                return value;
            }
        }
        return null;
    }

    private String extractRank(String message) {
        Matcher matcher = RANK_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }
        double value = Double.parseDouble(matcher.group(2));
        if ("万".equals(matcher.group(3))) {
            value = value * 10000D;
        }
        return String.valueOf((int) Math.round(value));
    }

    private String extractAdmissionYear(String message) {
        Matcher matcher = YEAR_PATTERN.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractSubjectCategory(String message) {
        if (message.contains("物理类")) {
            return "物理类";
        }
        if (message.contains("历史类")) {
            return "历史类";
        }
        if (message.contains("理科")) {
            return "理科";
        }
        if (message.contains("文科")) {
            return "文科";
        }
        if (message.contains("物理")) {
            return "物理类";
        }
        if (message.contains("历史")) {
            return "历史类";
        }
        return null;
    }

    private String extractProvince(String message) {
        String[] provinces = {"河南", "河北", "山东", "山西", "陕西", "湖北", "湖南", "江苏", "浙江", "安徽", "江西",
                "福建", "广东", "广西", "四川", "重庆", "云南", "贵州", "辽宁", "吉林", "黑龙江", "北京", "天津",
                "上海", "海南", "甘肃", "青海", "宁夏", "新疆", "内蒙古", "西藏"};
        return extractFirstContains(message, provinces);
    }

    private String extractBatchName(String message) {
        String[] batches = {"本科提前批", "本科一批", "本科二批", "专科提前批", "专科批", "高职高专批", "一批",
                "二批", "本科批"};
        return extractFirstContains(message, batches);
    }

    private String extractSelectionRequirement(String message) {
        String[] selections = {"不限", "物理化学", "物化", "物理", "历史", "化学", "生物", "政治", "地理"};
        return extractFirstContains(message, selections);
    }

    private String extractCollegeNature(String message) {
        String[] natures = {"公办", "民办", "独立学院", "中外合作"};
        return extractFirstContains(message, natures);
    }

    private String extractMaxTuitionFee(String message) {
        Matcher matcher = TUITION_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }
        double value = Double.parseDouble(matcher.group(2));
        String unit = matcher.group(3);
        if ("万".equals(unit)) {
            value = value * 10000D;
        } else if ("千".equals(unit)) {
            value = value * 1000D;
        }
        return String.valueOf((int) Math.round(value));
    }

    private String extractMajorPreference(String message) {
        List<String> keywords = new ArrayList<>();
        String[] majors = {"计算机", "软件", "电子信息", "电气", "自动化", "人工智能", "数据科学", "临床",
                "口腔", "医学", "护理", "师范", "法学", "汉语言", "会计", "财务", "金融", "机械"};
        for (String major : majors) {
            if (message.contains(major)) {
                keywords.add(major);
            }
        }
        Matcher matcher = Pattern.compile("([\\u4e00-\\u9fa5A-Za-z0-9、，,]+)优先").matcher(message);
        if (matcher.find()) {
            String candidate = matcher.group(1);
            if (!candidate.contains("公办") && !candidate.contains("民办") && !candidate.contains("城市")) {
                keywords.add(candidate);
            }
        }
        return keywords.isEmpty() ? null : String.join(",", keywords);
    }

    private String extractMajorNameFilter(String message, String majorPreference) {
        if (!StringUtils.hasText(majorPreference)) {
            return null;
        }
        if (message.contains("只看") || message.contains("限定") || message.contains("必须")) {
            return majorPreference;
        }
        return null;
    }

    private String extractCityPreference(String message) {
        List<String> keywords = new ArrayList<>();
        String[] cities = {"郑州", "洛阳", "开封", "南阳", "新乡", "焦作", "许昌", "安阳", "商丘", "周口",
                "信阳", "驻马店", "平顶山", "濮阳", "三门峡", "鹤壁", "漯河", "济源", "北京", "上海",
                "广州", "深圳", "杭州", "南京", "武汉", "西安", "成都", "重庆", "天津", "青岛", "苏州"};
        for (String city : cities) {
            if (message.contains(city)) {
                keywords.add(city);
            }
        }
        return keywords.isEmpty() ? null : String.join(",", keywords);
    }

    private String extractFirstContains(String message, String[] candidates) {
        for (String candidate : candidates) {
            if (message.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
