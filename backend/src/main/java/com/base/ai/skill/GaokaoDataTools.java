package com.base.ai.skill;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.gaokao.dto.AdmissionPlanQueryRequest;
import com.base.gaokao.dto.CollegeAdmissionScoreResponse;
import com.base.gaokao.dto.CollegeScoreQueryRequest;
import com.base.gaokao.dto.GaokaoBaseQueryRequest;
import com.base.gaokao.dto.MajorAdmissionPlanResponse;
import com.base.gaokao.dto.MajorAdmissionScoreResponse;
import com.base.gaokao.dto.MajorScoreQueryRequest;
import com.base.gaokao.service.GaokaoDataService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高考数据 AI 工具，暴露给 LangChain4j Function Calling 使用。
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GaokaoDataTools {

    private static final int DEFAULT_ADMISSION_YEAR = 2025;

    private static final String DEFAULT_PROVINCE_CODE = "410000";

    private static final int DEFAULT_QUERY_SIZE = 20;

    private static final int DEFAULT_RECOMMEND_SIZE = 10;

    private static final int MAX_QUERY_SIZE = 100;

    private static final int MAX_RECOMMEND_SIZE = 50;

    private static final Pattern TUITION_NUMBER_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    private final GaokaoDataService gaokaoDataService;

    /**
     * 查询院校录取分数线。
     *
     * @param subjectCategory 科类
     * @param scoreStart 最低分起始值
     * @param scoreEnd 最低分结束值
     * @param candidateProvince 考生省份编码或名称
     * @param admissionYear 年份
     * @param batchName 批次
     * @param collegeName 院校名称
     * @param selectionRequirement 选科要求
     * @param size 返回条数
     * @return JSON 字符串
     */
    @Tool("查询高考院校录取分数线。用户问某个分数段有哪些学校、学校最低分、最低位次、985/211、院校性质时使用。")
    public String queryCollegeAdmissionScores(
            @P("科类，例如 理科、文科、物理类、历史类") String subjectCategory,
            @P("最低分起始值，例如 380，可为空") String scoreStart,
            @P("最低分结束值，例如 420，可为空") String scoreEnd,
            @P("考生省份编码或名称，默认河南/410000") String candidateProvince,
            @P("录取年份，默认2025") String admissionYear,
            @P("批次，例如 本科二批、专科批，可为空") String batchName,
            @P("院校名称关键词，可为空") String collegeName,
            @P("选科要求，可为空") String selectionRequirement,
            @P("返回条数，默认20，最大100") String size) {
        log.info("AI 高考工具调用 queryCollegeAdmissionScores({}, {}, {})", subjectCategory, scoreStart, scoreEnd);
        try {
            CollegeScoreQueryRequest request = new CollegeScoreQueryRequest();
            applyCommonRequest(request, subjectCategory, candidateProvince, admissionYear, batchName,
                    collegeName, null, selectionRequirement, size, DEFAULT_QUERY_SIZE);
            request.setMinScoreStart(parseInteger(scoreStart, null, 0, 750));
            request.setMinScoreEnd(parseInteger(scoreEnd, null, 0, 750));

            Page<CollegeAdmissionScoreResponse> page = gaokaoDataService.pageCollegeScores(request);
            return buildPageResult(request, page);
        } catch (Exception e) {
            log.warn("AI 高考工具查询院校分数失败: {}", e.getMessage());
            return buildError("查询院校录取分数线失败：" + e.getMessage());
        }
    }

    /**
     * 查询专业录取分数线。
     *
     * @param subjectCategory 科类
     * @param scoreStart 最低分起始值
     * @param scoreEnd 最低分结束值
     * @param candidateProvince 考生省份编码或名称
     * @param admissionYear 年份
     * @param batchName 批次
     * @param collegeName 院校名称
     * @param majorName 专业名称
     * @param selectionRequirement 选科要求
     * @param size 返回条数
     * @return JSON 字符串
     */
    @Tool("查询高考专业录取分数线。用户问某分数可报哪些学校和专业、专业最低分、最低位次、录取人数时优先使用。")
    public String queryMajorAdmissionScores(
            @P("科类，例如 理科、文科、物理类、历史类") String subjectCategory,
            @P("最低分起始值，例如 380，可为空") String scoreStart,
            @P("最低分结束值，例如 420，可为空") String scoreEnd,
            @P("考生省份编码或名称，默认河南/410000") String candidateProvince,
            @P("录取年份，默认2025") String admissionYear,
            @P("批次，例如 本科二批、专科批，可为空") String batchName,
            @P("院校名称关键词，可为空") String collegeName,
            @P("专业名称关键词，可为空") String majorName,
            @P("选科要求，可为空") String selectionRequirement,
            @P("返回条数，默认20，最大100") String size) {
        log.info("AI 高考工具调用 queryMajorAdmissionScores({}, {}, {})", subjectCategory, scoreStart, scoreEnd);
        try {
            MajorScoreQueryRequest request = new MajorScoreQueryRequest();
            applyCommonRequest(request, subjectCategory, candidateProvince, admissionYear, batchName,
                    collegeName, majorName, selectionRequirement, size, DEFAULT_QUERY_SIZE);
            request.setMinScoreStart(parseInteger(scoreStart, null, 0, 750));
            request.setMinScoreEnd(parseInteger(scoreEnd, null, 0, 750));

            Page<MajorAdmissionScoreResponse> page = gaokaoDataService.pageMajorScores(request);
            return buildPageResult(request, page);
        } catch (Exception e) {
            log.warn("AI 高考工具查询专业分数失败: {}", e.getMessage());
            return buildError("查询专业录取分数线失败：" + e.getMessage());
        }
    }

    /**
     * 查询专业招生计划。
     *
     * @param subjectCategory 科类
     * @param candidateProvince 考生省份编码或名称
     * @param admissionYear 年份
     * @param batchName 批次
     * @param collegeName 院校名称
     * @param majorName 专业名称
     * @param selectionRequirement 选科要求
     * @param size 返回条数
     * @return JSON 字符串
     */
    @Tool("查询高考专业招生计划。用户问招生人数、学制、学费、某校某专业计划数，或报考推荐需要补充计划信息时使用。")
    public String queryAdmissionPlans(
            @P("科类，例如 理科、文科、物理类、历史类") String subjectCategory,
            @P("考生省份编码或名称，默认河南/410000") String candidateProvince,
            @P("招生年份，默认2025") String admissionYear,
            @P("批次，例如 本科二批、专科批，可为空") String batchName,
            @P("院校名称关键词，可为空") String collegeName,
            @P("专业名称关键词，可为空") String majorName,
            @P("选科要求，可为空") String selectionRequirement,
            @P("返回条数，默认20，最大100") String size) {
        log.info("AI 高考工具调用 queryAdmissionPlans({}, {})", subjectCategory, majorName);
        try {
            AdmissionPlanQueryRequest request = new AdmissionPlanQueryRequest();
            applyCommonRequest(request, subjectCategory, candidateProvince, admissionYear, batchName,
                    collegeName, majorName, selectionRequirement, size, DEFAULT_QUERY_SIZE);

            Page<MajorAdmissionPlanResponse> page = gaokaoDataService.pageAdmissionPlans(request);
            return buildPageResult(request, page);
        } catch (Exception e) {
            log.warn("AI 高考工具查询招生计划失败: {}", e.getMessage());
            return buildError("查询专业招生计划失败：" + e.getMessage());
        }
    }

    /**
     * 根据分数、位次和偏好推荐学校和专业。
     *
     * @param subjectCategory 科类
     * @param score 考生分数
     * @param candidateRank 考生位次
     * @param candidateProvince 考生省份编码或名称
     * @param admissionYear 年份
     * @param batchName 批次
     * @param majorName 专业名称硬过滤
     * @param majorPreference 专业偏好
     * @param cityPreference 城市或地区偏好
     * @param collegeNature 院校性质过滤
     * @param maxTuitionFee 最大学费
     * @param selectionRequirement 选科要求
     * @param size 推荐学校数
     * @return JSON 字符串
     */
    @Tool("按高考分数、位次和偏好推荐可报考学校与专业。用户输入如“理科400分推荐10所”、"
            + "“位次12万，计算机优先，只看公办，学费8000以内”时优先调用本工具。")
    public String recommendGaokaoSchoolMajors(
            @P("科类，例如 理科、文科、物理类、历史类") String subjectCategory,
            @P("考生分数，例如 400") String score,
            @P("考生位次，例如 120000，可为空；未提供时会基于历史近分录取记录粗略估算") String candidateRank,
            @P("考生省份编码或名称，默认河南/410000") String candidateProvince,
            @P("录取/招生年份，默认2025") String admissionYear,
            @P("批次，例如 本科二批、专科批，可为空") String batchName,
            @P("专业名称硬过滤关键词，例如 计算机；可为空") String majorName,
            @P("专业偏好，多个用逗号分隔，例如 计算机,电子信息；用于加权排序") String majorPreference,
            @P("城市或地区偏好，多个用逗号分隔，例如 郑州,洛阳,河南；用于加权排序") String cityPreference,
            @P("院校性质过滤，例如 公办、民办、独立学院；可为空") String collegeNature,
            @P("最大学费，单位元/年，例如 8000；可为空") String maxTuitionFee,
            @P("选科要求，可为空") String selectionRequirement,
            @P("推荐学校数，默认10，最大50") String size) {
        log.info("AI 高考工具调用 recommendGaokaoSchoolMajors({}, {}, {})", subjectCategory, score, size);
        try {
            Integer candidateScore = parseInteger(score, null, 0, 750);
            if (candidateScore == null) {
                return buildError("请提供有效的高考分数，例如 400");
            }

            int limit = parseInteger(size, DEFAULT_RECOMMEND_SIZE, 1, MAX_RECOMMEND_SIZE);
            RecommendationOptions options = buildRecommendationOptions(majorPreference, cityPreference,
                    collegeNature, maxTuitionFee);
            CandidateRankInfo rankInfo = resolveCandidateRank(candidateRank, candidateScore, subjectCategory,
                    candidateProvince, admissionYear, batchName, selectionRequirement);

            Page<MajorAdmissionPlanResponse> planPage = queryPlansForRecommend(subjectCategory, candidateProvince,
                    admissionYear, batchName, majorName, selectionRequirement, Math.min(limit * 2, MAX_QUERY_SIZE));
            Map<String, MajorAdmissionPlanResponse> planMap = indexPlans(planPage.getRecords());

            Set<String> usedKeys = new HashSet<>();
            List<Map<String, Object>> candidates = new ArrayList<>(limit * 3);
            collectBucketCandidates(candidates, usedKeys, "可冲", candidateScore, rankInfo.getRank(),
                    buildRushPage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                            selectionRequirement, collegeNature, candidateScore, rankInfo.getRank(), limit),
                    planMap, options);
            collectBucketCandidates(candidates, usedKeys, "较稳", candidateScore, rankInfo.getRank(),
                    buildStablePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                            selectionRequirement, collegeNature, candidateScore, rankInfo.getRank(), limit),
                    planMap, options);
            collectBucketCandidates(candidates, usedKeys, "保底", candidateScore, rankInfo.getRank(),
                    buildSafePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                            selectionRequirement, collegeNature, candidateScore, rankInfo.getRank(), limit),
                    planMap, options);

            if (candidates.size() < limit) {
                collectBucketCandidates(candidates, usedKeys, "补充参考", candidateScore, rankInfo.getRank(),
                        queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                                selectionRequirement, collegeNature, candidateScore - 60, candidateScore + 30,
                                null, null, "minScore", "desc", limit),
                        planMap, options);
            }

            sortRecommendations(candidates);
            List<Map<String, Object>> recommendations = trimRecommendationsByCollege(candidates, limit);
            Page<CollegeAdmissionScoreResponse> collegePage = queryCollegeScorePage(subjectCategory, candidateProvince,
                    admissionYear, batchName, selectionRequirement, collegeNature,
                    candidateScore - 40, candidateScore + 20, limit);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("query", buildRecommendQuery(subjectCategory, candidateScore, candidateProvince,
                    admissionYear, batchName, majorName, selectionRequirement, limit, options, rankInfo));
            result.put("rankConversion", rankInfo.toMap());
            result.put("recommendRule", buildRecommendRule(rankInfo));
            result.put("recommendationCount", recommendations.size());
            result.put("recommendations", recommendations);
            result.put("collegeScoreReferences", collegePage.getRecords());
            result.put("admissionPlanReferences", planPage.getRecords());
            result.put("notice",
                    "推荐结果基于历史录取分数、最低位次和招生计划，仅供参考，实际报考需结合当年一分一段表、招生章程和个人偏好。");
            return JSON.toJSONString(result);
        } catch (Exception e) {
            log.warn("AI 高考工具推荐失败: {}", e.getMessage());
            return buildError("推荐学校和专业失败：" + e.getMessage());
        }
    }

    private Page<MajorAdmissionScoreResponse> buildRushPage(String subjectCategory, String candidateProvince,
                                                            String admissionYear, String batchName,
                                                            String majorName, String selectionRequirement,
                                                            String collegeNature, int score, Integer rank,
                                                            int size) {
        if (rank != null) {
            int rankStart = Math.max(1, (int) Math.round(rank * 0.85D));
            int rankEnd = Math.max(rankStart, rank - 1);
            return queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                    selectionRequirement, collegeNature, null, null, rankStart, rankEnd, "minRank", "desc", size);
        }
        return queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                selectionRequirement, collegeNature, score + 1, score + 20, null, null, "minScore", "asc", size);
    }

    private Page<MajorAdmissionScoreResponse> buildStablePage(String subjectCategory, String candidateProvince,
                                                              String admissionYear, String batchName,
                                                              String majorName, String selectionRequirement,
                                                              String collegeNature, int score, Integer rank,
                                                              int size) {
        if (rank != null) {
            int rankStart = rank;
            int rankEnd = Math.max(rankStart, (int) Math.round(rank * 1.15D));
            return queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                    selectionRequirement, collegeNature, null, null, rankStart, rankEnd, "minRank", "asc", size);
        }
        return queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                selectionRequirement, collegeNature, score - 15, score, null, null, "minScore", "desc", size);
    }

    private Page<MajorAdmissionScoreResponse> buildSafePage(String subjectCategory, String candidateProvince,
                                                            String admissionYear, String batchName,
                                                            String majorName, String selectionRequirement,
                                                            String collegeNature, int score, Integer rank,
                                                            int size) {
        if (rank != null) {
            int rankStart = Math.max(rank + 1, (int) Math.round(rank * 1.15D) + 1);
            int rankEnd = Math.max(rankStart, (int) Math.round(rank * 1.40D));
            return queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                    selectionRequirement, collegeNature, null, null, rankStart, rankEnd, "minRank", "asc", size);
        }
        return queryMajorScorePage(subjectCategory, candidateProvince, admissionYear, batchName, majorName,
                selectionRequirement, collegeNature, score - 40, score - 16, null, null, "minScore", "desc", size);
    }

    private Page<MajorAdmissionScoreResponse> queryMajorScorePage(String subjectCategory, String candidateProvince,
                                                                  String admissionYear, String batchName,
                                                                  String majorName, String selectionRequirement,
                                                                  String collegeNature, Integer scoreStart,
                                                                  Integer scoreEnd, Integer rankStart,
                                                                  Integer rankEnd, String sortField,
                                                                  String sortOrder, int size) {
        MajorScoreQueryRequest request = new MajorScoreQueryRequest();
        applyCommonRequest(request, subjectCategory, candidateProvince, admissionYear, batchName,
                null, majorName, selectionRequirement, String.valueOf(size), size);
        request.setMinScoreStart(scoreStart);
        request.setMinScoreEnd(scoreEnd);
        request.setMinRankStart(rankStart);
        request.setMinRankEnd(rankEnd);
        request.setSortField(sortField);
        request.setSortOrder(sortOrder);
        Page<MajorAdmissionScoreResponse> page = gaokaoDataService.pageMajorScores(request);
        if (hasRecords(page)) {
            return page;
        }
        String fallbackSubjectCategory = resolveSubjectCategoryFallback(subjectCategory);
        if (StringUtils.hasText(fallbackSubjectCategory)) {
            request.setSubjectCategory(fallbackSubjectCategory);
            return gaokaoDataService.pageMajorScores(request);
        }
        return page;
    }

    private Page<CollegeAdmissionScoreResponse> queryCollegeScorePage(String subjectCategory, String candidateProvince,
                                                                      String admissionYear, String batchName,
                                                                      String selectionRequirement,
                                                                      String collegeNature,
                                                                      Integer scoreStart, Integer scoreEnd,
                                                                      int size) {
        CollegeScoreQueryRequest request = new CollegeScoreQueryRequest();
        applyCommonRequest(request, subjectCategory, candidateProvince, admissionYear, batchName,
                null, null, selectionRequirement, String.valueOf(size), size);
        request.setMinScoreStart(scoreStart);
        request.setMinScoreEnd(scoreEnd);
        request.setSortField("minScore");
        request.setSortOrder("desc");
        Page<CollegeAdmissionScoreResponse> page = gaokaoDataService.pageCollegeScores(request);
        if (hasRecords(page)) {
            return page;
        }
        String fallbackSubjectCategory = resolveSubjectCategoryFallback(subjectCategory);
        if (StringUtils.hasText(fallbackSubjectCategory)) {
            request.setSubjectCategory(fallbackSubjectCategory);
            return gaokaoDataService.pageCollegeScores(request);
        }
        return page;
    }

    private Page<MajorAdmissionPlanResponse> queryPlansForRecommend(String subjectCategory, String candidateProvince,
                                                                    String admissionYear, String batchName,
                                                                    String majorName, String selectionRequirement,
                                                                    int size) {
        AdmissionPlanQueryRequest request = new AdmissionPlanQueryRequest();
        applyCommonRequest(request, subjectCategory, candidateProvince, admissionYear, batchName,
                null, majorName, selectionRequirement, String.valueOf(size), size);
        request.setSortField("planCount");
        request.setSortOrder("desc");
        Page<MajorAdmissionPlanResponse> page = gaokaoDataService.pageAdmissionPlans(request);
        if (hasRecords(page)) {
            return page;
        }
        String fallbackSubjectCategory = resolveSubjectCategoryFallback(subjectCategory);
        if (StringUtils.hasText(fallbackSubjectCategory)) {
            request.setSubjectCategory(fallbackSubjectCategory);
            return gaokaoDataService.pageAdmissionPlans(request);
        }
        return page;
    }

    private CandidateRankInfo resolveCandidateRank(String candidateRank, int score, String subjectCategory,
                                                   String candidateProvince, String admissionYear, String batchName,
                                                   String selectionRequirement) {
        Integer parsedRank = parseInteger(candidateRank, null, 1, 10000000);
        if (parsedRank != null) {
            return new CandidateRankInfo(parsedRank, "USER_PROVIDED", "高", "用户提供位次，推荐优先按位次区间匹配。");
        }

        Page<MajorAdmissionScoreResponse> page = queryMajorScorePage(subjectCategory, candidateProvince,
                admissionYear, batchName, null, selectionRequirement, null, score - 2, score + 2,
                null, null, "minScore", "desc", 50);
        List<Integer> ranks = new ArrayList<>();
        if (page != null && page.getRecords() != null) {
            for (MajorAdmissionScoreResponse record : page.getRecords()) {
                if (record.getMinRank() != null && record.getMinRank() > 0) {
                    ranks.add(record.getMinRank());
                }
            }
        }
        if (ranks.isEmpty()) {
            return new CandidateRankInfo(null, "NOT_AVAILABLE", "无",
                    "未提供位次，且近分历史记录不足，推荐退回按分数区间匹配。");
        }
        Collections.sort(ranks);
        Integer estimatedRank = ranks.get(ranks.size() / 2);
        String confidence = ranks.size() >= 20 ? "中" : "低";
        return new CandidateRankInfo(estimatedRank, "ESTIMATED_BY_SCORE", confidence,
                "未提供位次，使用同科类近分录取记录的最低位次中位数粗略估算，不等同于当年一分一段表。");
    }

    private void collectBucketCandidates(List<Map<String, Object>> candidates, Set<String> usedKeys, String bucket,
                                         int candidateScore, Integer candidateRank,
                                         Page<MajorAdmissionScoreResponse> page,
                                         Map<String, MajorAdmissionPlanResponse> planMap,
                                         RecommendationOptions options) {
        if (page == null || page.getRecords() == null) {
            return;
        }
        for (MajorAdmissionScoreResponse record : page.getRecords()) {
            String key = buildMajorKey(record.getCollegeCode(), record.getMajorCode(),
                    record.getMajorName(), record.getMajorGroupCode());
            if (usedKeys.contains(key)) {
                continue;
            }
            MajorAdmissionPlanResponse plan = planMap.get(key);
            if (!matchesRecommendationOptions(record, plan, options)) {
                continue;
            }
            usedKeys.add(key);
            candidates.add(buildRecommendation(record, plan, bucket, candidateScore, candidateRank, options));
        }
    }

    private boolean matchesRecommendationOptions(MajorAdmissionScoreResponse score, MajorAdmissionPlanResponse plan,
                                                 RecommendationOptions options) {
        if (!options.getCollegeNatureKeywords().isEmpty() && StringUtils.hasText(score.getCollegeNature())
                && !containsAnyKeyword(score.getCollegeNature(), options.getCollegeNatureKeywords())) {
            return false;
        }
        if (options.getMaxTuitionFee() == null) {
            return true;
        }
        Integer tuitionFee = plan != null ? parseTuitionFee(plan.getTuitionFee()) : null;
        return tuitionFee == null || tuitionFee <= options.getMaxTuitionFee();
    }

    private Map<String, Object> buildRecommendation(MajorAdmissionScoreResponse score,
                                                    MajorAdmissionPlanResponse plan,
                                                    String bucket, int candidateScore,
                                                    Integer candidateRank,
                                                    RecommendationOptions options) {
        Integer tuitionFeeValue = plan != null ? parseTuitionFee(plan.getTuitionFee()) : null;
        boolean majorMatched = matchesMajorPreference(score, options);
        boolean cityMatched = matchesCityPreference(score, plan, options);
        boolean collegeNatureMatched = options.getCollegeNatureKeywords().isEmpty()
                || containsAnyKeyword(score.getCollegeNature(), options.getCollegeNatureKeywords());
        Integer recommendScore = calculateRecommendScore(score, plan, bucket, candidateScore, candidateRank,
                options, tuitionFeeValue, majorMatched, cityMatched, collegeNatureMatched);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("bucket", bucket);
        item.put("recommendScore", recommendScore);
        item.put("collegeName", score.getCollegeName());
        item.put("collegeCode", score.getCollegeCode());
        item.put("majorName", score.getMajorName());
        item.put("majorCode", score.getMajorCode());
        item.put("majorGroupCode", score.getMajorGroupCode());
        item.put("subjectCategory", score.getSubjectCategory());
        item.put("batchName", score.getBatchName());
        item.put("selectionRequirement", score.getSelectionRequirement());
        item.put("admissionYear", score.getAdmissionYear());
        item.put("candidateProvinceName", score.getCandidateProvinceName());
        item.put("minScore", score.getMinScore());
        item.put("scoreDiff", score.getMinScore() != null ? score.getMinScore() - candidateScore : null);
        item.put("minRank", score.getMinRank());
        item.put("rankDiff", buildRankDiff(score.getMinRank(), candidateRank));
        item.put("admissionCount", score.getAdmissionCount());
        item.put("collegeProvinceName", score.getCollegeProvinceName());
        item.put("collegeNature", score.getCollegeNature());
        item.put("is985", score.getIs985());
        item.put("is211", score.getIs211());
        item.put("majorRemark", score.getMajorRemark());
        item.put("majorPreferenceMatched", majorMatched);
        item.put("cityPreferenceMatched", cityMatched);
        item.put("collegeNatureMatched", collegeNatureMatched);
        item.put("tuitionFilterMatched", buildTuitionFilterMatched(tuitionFeeValue, options.getMaxTuitionFee()));
        if (plan != null) {
            item.put("planCount", plan.getPlanCount());
            item.put("studyYears", plan.getStudyYears());
            item.put("tuitionFee", plan.getTuitionFee());
            item.put("tuitionFeeValue", tuitionFeeValue);
        }
        return item;
    }

    private Integer calculateRecommendScore(MajorAdmissionScoreResponse score, MajorAdmissionPlanResponse plan,
                                            String bucket, int candidateScore, Integer candidateRank,
                                            RecommendationOptions options, Integer tuitionFeeValue,
                                            boolean majorMatched, boolean cityMatched,
                                            boolean collegeNatureMatched) {
        int result = bucketBaseScore(bucket);
        result += calculateScoreProximityScore(score.getMinScore(), candidateScore);
        result += calculateRankProximityScore(score.getMinRank(), candidateRank);
        if (majorMatched) {
            result += 25;
        }
        if (cityMatched) {
            result += 12;
        }
        if (collegeNatureMatched && !options.getCollegeNatureKeywords().isEmpty()) {
            result += 8;
        }
        result += calculateTuitionScore(tuitionFeeValue, options.getMaxTuitionFee());
        result += calculatePlanScore(plan);
        if (Integer.valueOf(1).equals(score.getIs985())) {
            result += 5;
        } else if (Integer.valueOf(1).equals(score.getIs211())) {
            result += 3;
        }
        return result;
    }

    private int bucketBaseScore(String bucket) {
        if ("较稳".equals(bucket)) {
            return 35;
        }
        if ("保底".equals(bucket)) {
            return 30;
        }
        if ("可冲".equals(bucket)) {
            return 20;
        }
        return 10;
    }

    private int calculateScoreProximityScore(Integer minScore, int candidateScore) {
        if (minScore == null) {
            return 0;
        }
        int diff = Math.abs(minScore - candidateScore);
        return Math.max(0, 20 - Math.min(diff, 20));
    }

    private int calculateRankProximityScore(Integer minRank, Integer candidateRank) {
        if (minRank == null || candidateRank == null || candidateRank <= 0) {
            return 0;
        }
        int diff = Math.abs(minRank - candidateRank);
        int percent = (int) Math.round(diff * 100D / candidateRank);
        return Math.max(0, 25 - Math.min(percent * 2, 25));
    }

    private int calculateTuitionScore(Integer tuitionFeeValue, Integer maxTuitionFee) {
        if (tuitionFeeValue == null) {
            return 0;
        }
        if (maxTuitionFee == null) {
            return tuitionFeeValue <= 8000 ? 3 : 0;
        }
        if (tuitionFeeValue > maxTuitionFee) {
            return 0;
        }
        int remainPercent = (int) Math.round((maxTuitionFee - tuitionFeeValue) * 100D / maxTuitionFee);
        return 6 + Math.min(6, Math.max(0, remainPercent / 20));
    }

    private int calculatePlanScore(MajorAdmissionPlanResponse plan) {
        if (plan == null || plan.getPlanCount() == null) {
            return 0;
        }
        return Math.min(10, Math.max(1, plan.getPlanCount() / 5));
    }

    private boolean matchesMajorPreference(MajorAdmissionScoreResponse score, RecommendationOptions options) {
        if (options.getMajorPreferenceKeywords().isEmpty()) {
            return false;
        }
        List<String> texts = new ArrayList<>(3);
        texts.add(score.getMajorName());
        texts.add(score.getMajorRemark());
        texts.add(score.getCollegeName());
        return containsAnyKeyword(texts, options.getMajorPreferenceKeywords());
    }

    private boolean matchesCityPreference(MajorAdmissionScoreResponse score, MajorAdmissionPlanResponse plan,
                                          RecommendationOptions options) {
        if (options.getCityPreferenceKeywords().isEmpty()) {
            return false;
        }
        List<String> texts = new ArrayList<>(4);
        texts.add(score.getCollegeProvinceName());
        texts.add(score.getCollegeName());
        if (plan != null) {
            texts.add(plan.getCollegeName());
            texts.add(plan.getRemark());
        }
        return containsAnyKeyword(texts, options.getCityPreferenceKeywords());
    }

    private Boolean buildTuitionFilterMatched(Integer tuitionFeeValue, Integer maxTuitionFee) {
        if (maxTuitionFee == null || tuitionFeeValue == null) {
            return null;
        }
        return tuitionFeeValue <= maxTuitionFee;
    }

    private Integer buildRankDiff(Integer minRank, Integer candidateRank) {
        if (minRank == null || candidateRank == null) {
            return null;
        }
        return minRank - candidateRank;
    }

    private void sortRecommendations(List<Map<String, Object>> recommendations) {
        Collections.sort(recommendations, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> left, Map<String, Object> right) {
                return Integer.compare(readRecommendScore(right), readRecommendScore(left));
            }
        });
    }

    private int readRecommendScore(Map<String, Object> item) {
        Object score = item.get("recommendScore");
        return score instanceof Integer ? (Integer) score : 0;
    }

    private List<Map<String, Object>> trimRecommendationsByCollege(List<Map<String, Object>> candidates, int limit) {
        List<Map<String, Object>> result = new ArrayList<>(limit);
        Set<String> usedColleges = new HashSet<>();
        for (Map<String, Object> candidate : candidates) {
            String collegeKey = buildCollegeKey(candidate);
            if (usedColleges.contains(collegeKey)) {
                continue;
            }
            usedColleges.add(collegeKey);
            result.add(candidate);
            if (result.size() >= limit) {
                return result;
            }
        }
        return result;
    }

    private String buildCollegeKey(Map<String, Object> candidate) {
        Object collegeCode = candidate.get("collegeCode");
        if (collegeCode != null && StringUtils.hasText(String.valueOf(collegeCode))) {
            return String.valueOf(collegeCode).trim();
        }
        Object collegeName = candidate.get("collegeName");
        return collegeName == null ? "" : String.valueOf(collegeName).trim();
    }

    private Map<String, MajorAdmissionPlanResponse> indexPlans(List<MajorAdmissionPlanResponse> plans) {
        Map<String, MajorAdmissionPlanResponse> planMap = new HashMap<>(64);
        if (plans == null || plans.isEmpty()) {
            return planMap;
        }
        for (MajorAdmissionPlanResponse plan : plans) {
            String key = buildMajorKey(plan.getCollegeCode(), plan.getMajorCode(),
                    plan.getMajorName(), plan.getMajorGroupCode());
            if (!planMap.containsKey(key)) {
                planMap.put(key, plan);
            }
        }
        return planMap;
    }

    private void applyCommonRequest(GaokaoBaseQueryRequest request, String subjectCategory, String candidateProvince,
                                    String admissionYear, String batchName, String collegeName, String majorName,
                                    String selectionRequirement, String size, int defaultSize) {
        ProvinceInfo provinceInfo = resolveProvince(candidateProvince);
        request.setAdmissionYear(parseInteger(admissionYear, DEFAULT_ADMISSION_YEAR, 2000, 2100));
        request.setCandidateProvinceCode(provinceInfo.getCode());
        request.setCandidateProvinceName(provinceInfo.getName());
        request.setSubjectCategory(normalizeSubjectCategory(subjectCategory));
        request.setBatchName(cleanText(batchName));
        request.setCollegeName(cleanText(collegeName));
        request.setSelectionRequirement(cleanText(selectionRequirement));
        request.setCurrent(1L);
        request.setSize(Long.valueOf(parseInteger(size, defaultSize, 1, MAX_QUERY_SIZE)));
        if (request instanceof MajorScoreQueryRequest) {
            ((MajorScoreQueryRequest) request).setMajorName(cleanText(majorName));
        }
        if (request instanceof AdmissionPlanQueryRequest) {
            ((AdmissionPlanQueryRequest) request).setMajorName(cleanText(majorName));
        }
    }

    private String buildPageResult(GaokaoBaseQueryRequest request, Page<?> page) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("query", request);
        result.put("page", buildPageMeta(page));
        result.put("records", page != null ? page.getRecords() : new ArrayList<>());
        return JSON.toJSONString(result);
    }

    private Map<String, Object> buildPageMeta(Page<?> page) {
        Map<String, Object> meta = new LinkedHashMap<>();
        if (page == null) {
            meta.put("current", 1L);
            meta.put("size", 0L);
            meta.put("total", 0L);
            meta.put("pages", 0L);
            return meta;
        }
        meta.put("current", page.getCurrent());
        meta.put("size", page.getSize());
        meta.put("total", page.getTotal());
        meta.put("pages", page.getPages());
        return meta;
    }

    private Map<String, Object> buildRecommendQuery(String subjectCategory, Integer score, String candidateProvince,
                                                    String admissionYear, String batchName, String majorName,
                                                    String selectionRequirement, int size,
                                                    RecommendationOptions options,
                                                    CandidateRankInfo rankInfo) {
        Map<String, Object> query = new LinkedHashMap<>();
        ProvinceInfo provinceInfo = resolveProvince(candidateProvince);
        query.put("subjectCategory", normalizeSubjectCategory(subjectCategory));
        query.put("score", score);
        query.put("candidateRank", rankInfo.getRank());
        query.put("candidateProvinceCode", provinceInfo.getCode());
        query.put("candidateProvinceName", provinceInfo.getName());
        query.put("admissionYear", parseInteger(admissionYear, DEFAULT_ADMISSION_YEAR, 2000, 2100));
        query.put("batchName", cleanText(batchName));
        query.put("majorName", cleanText(majorName));
        query.put("majorPreference", options.getMajorPreferenceKeywords());
        query.put("cityPreference", options.getCityPreferenceKeywords());
        query.put("collegeNature", options.getCollegeNatureKeywords());
        query.put("maxTuitionFee", options.getMaxTuitionFee());
        query.put("selectionRequirement", cleanText(selectionRequirement));
        query.put("size", size);
        return query;
    }

    private String buildRecommendRule(CandidateRankInfo rankInfo) {
        String bucketRule;
        if (rankInfo.getRank() != null) {
            bucketRule = "按位次优先匹配：可冲=历史最低位次优于考生位次约15%以内；较稳=考生位次到后15%；保底=后15%-40%。";
        } else {
            bucketRule = "按分数匹配：可冲=最低分高于考生分1-20分；较稳=低于或等于考生分0-15分；保底=低16-40分。";
        }
        return bucketRule + "综合分=分数/位次接近度 + 专业偏好 + 地区偏好 + 院校性质 + 学费 + 招生计划 + 985/211加分。";
    }

    private RecommendationOptions buildRecommendationOptions(String majorPreference, String cityPreference,
                                                             String collegeNature, String maxTuitionFee) {
        return new RecommendationOptions(parsePreferenceKeywords(majorPreference),
                parsePreferenceKeywords(cityPreference), parsePreferenceKeywords(collegeNature),
                parseInteger(maxTuitionFee, null, 0, 1000000));
    }

    private List<String> parsePreferenceKeywords(String value) {
        List<String> keywords = new ArrayList<>();
        String cleaned = cleanText(value);
        if (!StringUtils.hasText(cleaned)) {
            return keywords;
        }
        String[] parts = cleaned.split("[,，、;；\\s]+");
        for (String part : parts) {
            String keyword = cleanText(part);
            if (StringUtils.hasText(keyword)) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

    private boolean containsAnyKeyword(List<String> texts, List<String> keywords) {
        for (String text : texts) {
            if (containsAnyKeyword(text, keywords)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAnyKeyword(String text, List<String> keywords) {
        if (!StringUtils.hasText(text) || keywords == null || keywords.isEmpty()) {
            return false;
        }
        String normalizedText = normalizeKeywordText(text);
        for (String keyword : keywords) {
            if (normalizedText.contains(normalizeKeywordText(keyword))) {
                return true;
            }
        }
        return false;
    }

    private String normalizeKeywordText(String text) {
        return text == null ? "" : text.replace(" ", "").toLowerCase();
    }

    private Integer parseTuitionFee(String tuitionFee) {
        String cleaned = cleanText(tuitionFee);
        if (!StringUtils.hasText(cleaned)) {
            return null;
        }
        Matcher matcher = TUITION_NUMBER_PATTERN.matcher(cleaned.replace(",", ""));
        if (!matcher.find()) {
            return null;
        }
        BigDecimal amount = new BigDecimal(matcher.group(1));
        if (cleaned.contains("万")) {
            amount = amount.multiply(new BigDecimal("10000"));
        }
        return amount.intValue();
    }

    private String buildError(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("error", message);
        return JSON.toJSONString(result);
    }

    private Integer parseInteger(String value, Integer defaultValue, int min, int max) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            int parsedValue = Integer.parseInt(value.trim().replaceAll("[^0-9-]", ""));
            if (parsedValue < min) {
                return min;
            }
            if (parsedValue > max) {
                return max;
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean hasRecords(Page<?> page) {
        return page != null && page.getRecords() != null && !page.getRecords().isEmpty();
    }

    private String resolveSubjectCategoryFallback(String subjectCategory) {
        String cleaned = cleanText(subjectCategory);
        if ("理科".equals(cleaned)) {
            return "物理类";
        }
        if ("文科".equals(cleaned)) {
            return "历史类";
        }
        return null;
    }

    private String normalizeSubjectCategory(String subjectCategory) {
        String cleaned = cleanText(subjectCategory);
        if (!StringUtils.hasText(cleaned)) {
            return null;
        }
        if ("物理".equals(cleaned)) {
            return "物理类";
        }
        if ("历史".equals(cleaned)) {
            return "历史类";
        }
        return cleaned;
    }

    private ProvinceInfo resolveProvince(String candidateProvince) {
        String cleaned = cleanText(candidateProvince);
        if (!StringUtils.hasText(cleaned)) {
            return new ProvinceInfo(DEFAULT_PROVINCE_CODE, null);
        }
        if (cleaned.matches("^\\d{6}$")) {
            return new ProvinceInfo(cleaned, null);
        }
        if (cleaned.contains("河南")) {
            return new ProvinceInfo(DEFAULT_PROVINCE_CODE, null);
        }
        return new ProvinceInfo(null, cleaned);
    }

    private String cleanText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String buildMajorKey(String collegeCode, String majorCode, String majorName, String majorGroupCode) {
        StringBuilder key = new StringBuilder();
        appendKeyPart(key, collegeCode);
        appendKeyPart(key, majorCode);
        appendKeyPart(key, majorName);
        appendKeyPart(key, majorGroupCode);
        return key.toString();
    }

    private void appendKeyPart(StringBuilder key, String value) {
        if (StringUtils.hasText(value)) {
            key.append(value.trim());
        }
        key.append('|');
    }

    /**
     * 高考推荐偏好参数。
     */
    private static class RecommendationOptions {

        private final List<String> majorPreferenceKeywords;

        private final List<String> cityPreferenceKeywords;

        private final List<String> collegeNatureKeywords;

        private final Integer maxTuitionFee;

        RecommendationOptions(List<String> majorPreferenceKeywords, List<String> cityPreferenceKeywords,
                              List<String> collegeNatureKeywords, Integer maxTuitionFee) {
            this.majorPreferenceKeywords = majorPreferenceKeywords;
            this.cityPreferenceKeywords = cityPreferenceKeywords;
            this.collegeNatureKeywords = collegeNatureKeywords;
            this.maxTuitionFee = maxTuitionFee;
        }

        List<String> getMajorPreferenceKeywords() {
            return majorPreferenceKeywords;
        }

        List<String> getCityPreferenceKeywords() {
            return cityPreferenceKeywords;
        }

        List<String> getCollegeNatureKeywords() {
            return collegeNatureKeywords;
        }

        Integer getMaxTuitionFee() {
            return maxTuitionFee;
        }
    }

    /**
     * 位次解析和估算结果。
     */
    private static class CandidateRankInfo {

        private final Integer rank;

        private final String source;

        private final String confidence;

        private final String description;

        CandidateRankInfo(Integer rank, String source, String confidence, String description) {
            this.rank = rank;
            this.source = source;
            this.confidence = confidence;
            this.description = description;
        }

        Integer getRank() {
            return rank;
        }

        Map<String, Object> toMap() {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("rank", rank);
            result.put("source", source);
            result.put("confidence", confidence);
            result.put("description", description);
            return result;
        }
    }

    /**
     * 省份解析结果。
     */
    private static class ProvinceInfo {

        private final String code;

        private final String name;

        ProvinceInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }

        String getCode() {
            return code;
        }

        String getName() {
            return name;
        }
    }
}
