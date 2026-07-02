package com.base.gaokao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.gaokao.dto.AdmissionPlanQueryRequest;
import com.base.gaokao.dto.CollegeAdmissionScoreResponse;
import com.base.gaokao.dto.CollegeScoreQueryRequest;
import com.base.gaokao.dto.GaokaoBaseQueryRequest;
import com.base.gaokao.dto.MajorAdmissionPlanResponse;
import com.base.gaokao.dto.MajorAdmissionScoreResponse;
import com.base.gaokao.dto.MajorScoreQueryRequest;
import com.base.gaokao.entity.CollegeAdmissionScore;
import com.base.gaokao.entity.MajorAdmissionPlan;
import com.base.gaokao.entity.MajorAdmissionScore;
import com.base.gaokao.mapper.CollegeAdmissionScoreMapper;
import com.base.gaokao.mapper.MajorAdmissionPlanMapper;
import com.base.gaokao.mapper.MajorAdmissionScoreMapper;
import com.base.gaokao.service.GaokaoDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 高考数据查询服务实现类
 *
 * @author base
 */
@Service
@RequiredArgsConstructor
public class GaokaoDataServiceImpl implements GaokaoDataService {

    private static final String ORDER_ASC = "asc";

    private static final String ORDER_DESC = "desc";

    private static final Map<String, String> COLLEGE_SCORE_SORT_FIELDS = buildCollegeScoreSortFields();

    private static final Map<String, String> MAJOR_SCORE_SORT_FIELDS = buildMajorScoreSortFields();

    private static final Map<String, String> ADMISSION_PLAN_SORT_FIELDS = buildAdmissionPlanSortFields();

    private final CollegeAdmissionScoreMapper collegeAdmissionScoreMapper;

    private final MajorAdmissionScoreMapper majorAdmissionScoreMapper;

    private final MajorAdmissionPlanMapper majorAdmissionPlanMapper;

    @Override
    public Page<CollegeAdmissionScoreResponse> pageCollegeScores(CollegeScoreQueryRequest request) {
        QueryWrapper<CollegeAdmissionScore> wrapper = new QueryWrapper<>();
        applyCommonConditions(wrapper, request);
        applyCollegeScoreConditions(wrapper, request);
        applyCollegeKeyword(wrapper, request.getKeyword());
        applyScoreDefaultSort(wrapper, request.getSortField(), request.getSortOrder(), COLLEGE_SCORE_SORT_FIELDS);

        Page<CollegeAdmissionScore> page = collegeAdmissionScoreMapper.selectPage(request.buildPage(), wrapper);
        return convertPage(page, CollegeAdmissionScoreResponse.class);
    }

    @Override
    public Page<MajorAdmissionScoreResponse> pageMajorScores(MajorScoreQueryRequest request) {
        QueryWrapper<MajorAdmissionScore> wrapper = new QueryWrapper<>();
        applyCommonConditions(wrapper, request);
        applyCollegeScoreConditions(wrapper, request);
        applyMajorConditions(wrapper, request.getMajorCode(), request.getMajorName(), request.getMajorRemark());
        applyMajorKeyword(wrapper, request.getKeyword());
        applyScoreDefaultSort(wrapper, request.getSortField(), request.getSortOrder(), MAJOR_SCORE_SORT_FIELDS);

        Page<MajorAdmissionScore> page = majorAdmissionScoreMapper.selectPage(request.buildPage(), wrapper);
        return convertPage(page, MajorAdmissionScoreResponse.class);
    }

    @Override
    public Page<MajorAdmissionPlanResponse> pageAdmissionPlans(AdmissionPlanQueryRequest request) {
        QueryWrapper<MajorAdmissionPlan> wrapper = new QueryWrapper<>();
        applyCommonConditions(wrapper, request);
        applyMajorConditions(wrapper, request.getMajorCode(), request.getMajorName(), request.getMajorRemark());
        applyRange(wrapper, "study_years", request.getStudyYearsStart(), request.getStudyYearsEnd(), "学制");
        applyRange(wrapper, "plan_count", request.getPlanCountStart(), request.getPlanCountEnd(), "计划人数");
        wrapper.eq(StringUtils.hasText(request.getTuitionFee()), "tuition_fee", request.getTuitionFee());
        applyMajorKeyword(wrapper, request.getKeyword());
        applyPlanDefaultSort(wrapper, request.getSortField(), request.getSortOrder(), ADMISSION_PLAN_SORT_FIELDS);

        Page<MajorAdmissionPlan> page = majorAdmissionPlanMapper.selectPage(request.buildPage(), wrapper);
        return convertPage(page, MajorAdmissionPlanResponse.class);
    }

    /**
     * 应用通用查询条件
     *
     * @param wrapper 查询包装器
     * @param request 查询请求
     * @param <T> 实体类型
     */
    private <T> void applyCommonConditions(QueryWrapper<T> wrapper, GaokaoBaseQueryRequest request) {
        wrapper.eq(request.getAdmissionYear() != null, "admission_year", request.getAdmissionYear())
                .eq(StringUtils.hasText(request.getCandidateProvinceCode()),
                        "candidate_province_code", request.getCandidateProvinceCode())
                .eq(StringUtils.hasText(request.getCandidateProvinceName()),
                        "candidate_province_name", request.getCandidateProvinceName())
                .eq(StringUtils.hasText(request.getBatchName()), "batch_name", request.getBatchName())
                .eq(StringUtils.hasText(request.getSubjectCategory()), "subject_category", request.getSubjectCategory())
                .eq(StringUtils.hasText(request.getCollegeCode()), "college_code", request.getCollegeCode())
                .like(StringUtils.hasText(request.getCollegeName()), "college_name", request.getCollegeName())
                .eq(StringUtils.hasText(request.getMajorGroupCode()), "major_group_code", request.getMajorGroupCode())
                .like(StringUtils.hasText(request.getSelectionRequirement()),
                        "selection_requirement", request.getSelectionRequirement());
    }

    /**
     * 应用院校和专业分数线通用条件
     *
     * @param wrapper 查询包装器
     * @param request 查询请求
     * @param <T> 实体类型
     */
    private <T> void applyCollegeScoreConditions(QueryWrapper<T> wrapper, CollegeScoreQueryRequest request) {
        wrapper.eq(StringUtils.hasText(request.getAdmissionType()), "admission_type", request.getAdmissionType())
                .eq(StringUtils.hasText(request.getCollegeProvinceName()),
                        "college_province_name", request.getCollegeProvinceName())
                .eq(StringUtils.hasText(request.getCollegeNature()), "college_nature", request.getCollegeNature())
                .eq(request.getIs985() != null, "is_985", request.getIs985())
                .eq(request.getIs211() != null, "is_211", request.getIs211());
        applyRange(wrapper, "min_score", request.getMinScoreStart(), request.getMinScoreEnd(), "最低分数");
        applyRange(wrapper, "min_rank", request.getMinRankStart(), request.getMinRankEnd(), "最低位次");
    }

    /**
     * 应用专业条件
     *
     * @param wrapper 查询包装器
     * @param majorCode 专业代码
     * @param majorName 专业名称
     * @param majorRemark 专业备注
     * @param <T> 实体类型
     */
    private <T> void applyMajorConditions(QueryWrapper<T> wrapper, String majorCode,
                                          String majorName, String majorRemark) {
        wrapper.eq(StringUtils.hasText(majorCode), "major_code", majorCode)
                .like(StringUtils.hasText(majorName), "major_name", majorName)
                .like(StringUtils.hasText(majorRemark), "major_remark", majorRemark);
    }

    /**
     * 应用院校关键词查询
     *
     * @param wrapper 查询包装器
     * @param keyword 关键词
     * @param <T> 实体类型
     */
    private <T> void applyCollegeKeyword(QueryWrapper<T> wrapper, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        wrapper.and(item -> item.like("college_code", keyword)
                .or().like("college_name", keyword)
                .or().like("major_group_code", keyword));
    }

    /**
     * 应用专业关键词查询
     *
     * @param wrapper 查询包装器
     * @param keyword 关键词
     * @param <T> 实体类型
     */
    private <T> void applyMajorKeyword(QueryWrapper<T> wrapper, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        wrapper.and(item -> item.like("college_code", keyword)
                .or().like("college_name", keyword)
                .or().like("major_group_code", keyword)
                .or().like("major_code", keyword)
                .or().like("major_name", keyword));
    }

    /**
     * 应用数值范围查询
     *
     * @param wrapper 查询包装器
     * @param columnName 列名
     * @param start 起始值
     * @param end 结束值
     * @param fieldName 字段中文名
     * @param <T> 实体类型
     */
    private <T> void applyRange(QueryWrapper<T> wrapper, String columnName,
                                Integer start, Integer end, String fieldName) {
        if (start != null && end != null && start > end) {
            throw new BusinessException(fieldName + "起始值不能大于结束值");
        }
        wrapper.ge(start != null, columnName, start)
                .le(end != null, columnName, end);
    }

    /**
     * 应用分数线默认排序
     *
     * @param wrapper 查询包装器
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     * @param sortFields 排序字段白名单
     * @param <T> 实体类型
     */
    private <T> void applyScoreDefaultSort(QueryWrapper<T> wrapper, String sortField,
                                           String sortOrder, Map<String, String> sortFields) {
        if (applyRequestSort(wrapper, sortField, sortOrder, sortFields)) {
            return;
        }
        wrapper.orderByDesc("min_score").orderByAsc("min_rank").orderByDesc("id");
    }

    /**
     * 应用招生计划默认排序
     *
     * @param wrapper 查询包装器
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     * @param sortFields 排序字段白名单
     * @param <T> 实体类型
     */
    private <T> void applyPlanDefaultSort(QueryWrapper<T> wrapper, String sortField,
                                          String sortOrder, Map<String, String> sortFields) {
        if (applyRequestSort(wrapper, sortField, sortOrder, sortFields)) {
            return;
        }
        wrapper.orderByDesc("plan_count").orderByDesc("id");
    }

    /**
     * 应用请求排序
     *
     * @param wrapper 查询包装器
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     * @param sortFields 排序字段白名单
     * @param <T> 实体类型
     * @return 是否应用了请求排序
     */
    private <T> boolean applyRequestSort(QueryWrapper<T> wrapper, String sortField,
                                         String sortOrder, Map<String, String> sortFields) {
        if (!StringUtils.hasText(sortField)) {
            return false;
        }
        String columnName = sortFields.get(sortField.trim());
        if (!StringUtils.hasText(columnName)) {
            throw new BusinessException("不支持的排序字段：" + sortField);
        }
        boolean asc = resolveSortAsc(sortOrder);
        wrapper.orderBy(true, asc, columnName).orderByDesc("id");
        return true;
    }

    /**
     * 解析排序方向
     *
     * @param sortOrder 排序方向
     * @return 是否升序
     */
    private boolean resolveSortAsc(String sortOrder) {
        if (!StringUtils.hasText(sortOrder)) {
            return false;
        }
        String order = sortOrder.trim().toLowerCase();
        if (ORDER_ASC.equals(order)) {
            return true;
        }
        if (ORDER_DESC.equals(order)) {
            return false;
        }
        throw new BusinessException("排序方向仅支持 asc 或 desc");
    }

    /**
     * 转换分页响应
     *
     * @param sourcePage 原始分页
     * @param responseType 响应类型
     * @param <S> 原始类型
     * @param <R> 响应类型
     * @return 响应分页
     */
    private <S, R> Page<R> convertPage(Page<S> sourcePage, Class<R> responseType) {
        Page<R> responsePage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        responsePage.setRecords(sourcePage.getRecords().stream()
                .map(source -> copyToResponse(source, responseType))
                .collect(Collectors.toList()));
        return responsePage;
    }

    /**
     * 复制实体到响应对象
     *
     * @param source 源对象
     * @param responseType 响应类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    private <R> R copyToResponse(Object source, Class<R> responseType) {
        try {
            R response = responseType.newInstance();
            BeanUtils.copyProperties(source, response);
            return response;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BusinessException("构建响应数据失败", e);
        }
    }

    /**
     * 构建院校分数排序字段白名单
     *
     * @return 排序字段白名单
     */
    private static Map<String, String> buildCollegeScoreSortFields() {
        Map<String, String> sortFields = buildCommonSortFields();
        putSortField(sortFields, "admissionType", "admission_type");
        putSortField(sortFields, "admissionCount", "admission_count");
        putSortField(sortFields, "minScore", "min_score");
        putSortField(sortFields, "minRank", "min_rank");
        putSortField(sortFields, "batchLineDiff", "batch_line_diff");
        putSortField(sortFields, "collegeProvinceName", "college_province_name");
        putSortField(sortFields, "collegeNature", "college_nature");
        putSortField(sortFields, "is985", "is_985");
        putSortField(sortFields, "is211", "is_211");
        return sortFields;
    }

    /**
     * 构建专业分数排序字段白名单
     *
     * @return 排序字段白名单
     */
    private static Map<String, String> buildMajorScoreSortFields() {
        Map<String, String> sortFields = buildCollegeScoreSortFields();
        putSortField(sortFields, "majorCode", "major_code");
        putSortField(sortFields, "majorName", "major_name");
        putSortField(sortFields, "majorRemark", "major_remark");
        return sortFields;
    }

    /**
     * 构建招生计划排序字段白名单
     *
     * @return 排序字段白名单
     */
    private static Map<String, String> buildAdmissionPlanSortFields() {
        Map<String, String> sortFields = buildCommonSortFields();
        putSortField(sortFields, "majorCode", "major_code");
        putSortField(sortFields, "majorName", "major_name");
        putSortField(sortFields, "majorRemark", "major_remark");
        putSortField(sortFields, "studyYears", "study_years");
        putSortField(sortFields, "tuitionFee", "tuition_fee");
        putSortField(sortFields, "planCount", "plan_count");
        return sortFields;
    }

    /**
     * 构建通用排序字段白名单
     *
     * @return 排序字段白名单
     */
    private static Map<String, String> buildCommonSortFields() {
        Map<String, String> sortFields = new HashMap<>(32);
        putSortField(sortFields, "id", "id");
        putSortField(sortFields, "admissionYear", "admission_year");
        putSortField(sortFields, "candidateProvinceCode", "candidate_province_code");
        putSortField(sortFields, "candidateProvinceName", "candidate_province_name");
        putSortField(sortFields, "batchName", "batch_name");
        putSortField(sortFields, "subjectCategory", "subject_category");
        putSortField(sortFields, "collegeCode", "college_code");
        putSortField(sortFields, "collegeName", "college_name");
        putSortField(sortFields, "majorGroupCode", "major_group_code");
        putSortField(sortFields, "selectionRequirement", "selection_requirement");
        putSortField(sortFields, "createTime", "create_time");
        putSortField(sortFields, "updateTime", "update_time");
        return sortFields;
    }

    /**
     * 添加排序字段及其数据库列别名
     *
     * @param sortFields 排序字段白名单
     * @param fieldName 接口字段名
     * @param columnName 数据库列名
     */
    private static void putSortField(Map<String, String> sortFields, String fieldName, String columnName) {
        sortFields.put(fieldName, columnName);
        sortFields.put(columnName, columnName);
    }
}
