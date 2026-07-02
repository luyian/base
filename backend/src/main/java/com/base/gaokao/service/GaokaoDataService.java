package com.base.gaokao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.gaokao.dto.AdmissionPlanQueryRequest;
import com.base.gaokao.dto.CollegeAdmissionScoreResponse;
import com.base.gaokao.dto.CollegeScoreQueryRequest;
import com.base.gaokao.dto.MajorAdmissionPlanResponse;
import com.base.gaokao.dto.MajorAdmissionScoreResponse;
import com.base.gaokao.dto.MajorScoreQueryRequest;

/**
 * 高考数据查询服务
 *
 * @author base
 */
public interface GaokaoDataService {

    /**
     * 分页查询院校录取分数线
     *
     * @param request 查询请求
     * @return 院校录取分数分页结果
     */
    Page<CollegeAdmissionScoreResponse> pageCollegeScores(CollegeScoreQueryRequest request);

    /**
     * 分页查询专业录取分数线
     *
     * @param request 查询请求
     * @return 专业录取分数分页结果
     */
    Page<MajorAdmissionScoreResponse> pageMajorScores(MajorScoreQueryRequest request);

    /**
     * 分页查询专业招生计划
     *
     * @param request 查询请求
     * @return 专业招生计划分页结果
     */
    Page<MajorAdmissionPlanResponse> pageAdmissionPlans(AdmissionPlanQueryRequest request);
}
