package com.base.gaokao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.gaokao.dto.AdmissionPlanQueryRequest;
import com.base.gaokao.dto.CollegeAdmissionScoreResponse;
import com.base.gaokao.dto.CollegeScoreQueryRequest;
import com.base.gaokao.dto.MajorAdmissionPlanResponse;
import com.base.gaokao.dto.MajorAdmissionScoreResponse;
import com.base.gaokao.dto.MajorScoreQueryRequest;
import com.base.gaokao.service.GaokaoDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 高考数据查询控制器
 *
 * @author base
 */
@Api(tags = "高考数据查询")
@RestController
@RequestMapping("/gaokao")
@RequiredArgsConstructor
public class GaokaoDataController {

    private final GaokaoDataService gaokaoDataService;

    /**
     * 分页查询院校录取分数线
     */
    @ApiOperation("分页查询院校录取分数线")
    @PostMapping("/college-scores/page")
    @PreAuthorize("hasAuthority('gaokao:college-score:list')")
    public Result<Page<CollegeAdmissionScoreResponse>> pageCollegeScores(
            @Valid @RequestBody CollegeScoreQueryRequest request) {
        return Result.success(gaokaoDataService.pageCollegeScores(request));
    }

    /**
     * 分页查询专业录取分数线
     */
    @ApiOperation("分页查询专业录取分数线")
    @PostMapping("/major-scores/page")
    @PreAuthorize("hasAuthority('gaokao:major-score:list')")
    public Result<Page<MajorAdmissionScoreResponse>> pageMajorScores(
            @Valid @RequestBody MajorScoreQueryRequest request) {
        return Result.success(gaokaoDataService.pageMajorScores(request));
    }

    /**
     * 分页查询专业招生计划
     */
    @ApiOperation("分页查询专业招生计划")
    @PostMapping("/admission-plans/page")
    @PreAuthorize("hasAuthority('gaokao:admission-plan:list')")
    public Result<Page<MajorAdmissionPlanResponse>> pageAdmissionPlans(
            @Valid @RequestBody AdmissionPlanQueryRequest request) {
        return Result.success(gaokaoDataService.pageAdmissionPlans(request));
    }
}
