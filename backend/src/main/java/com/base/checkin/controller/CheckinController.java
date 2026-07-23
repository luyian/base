package com.base.checkin.controller;

import com.base.checkin.dto.CalendarDayResponse;
import com.base.checkin.dto.CheckinPlanRequest;
import com.base.checkin.dto.CheckinPlanResponse;
import com.base.checkin.service.CheckinPlanService;
import com.base.checkin.service.CheckinRecordService;
import com.base.common.result.Result;
import com.base.system.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序打卡控制器
 *
 * @author base
 */
@Api(tags = "小程序打卡")
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinPlanService checkinPlanService;
    private final CheckinRecordService checkinRecordService;

    /**
     * 查询我的计划列表（含今日打卡状态）
     */
    @ApiOperation("查询我的计划列表")
    @GetMapping("/plan/list")
    @PreAuthorize("hasAuthority('checkin:plan:list')")
    public Result<List<CheckinPlanResponse>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(checkinPlanService.listByUserId(userId));
    }

    /**
     * 新增计划
     */
    @ApiOperation("新增计划")
    @PostMapping("/plan")
    @PreAuthorize("hasAuthority('checkin:plan:add')")
    public Result<Long> add(@Validated @RequestBody CheckinPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(checkinPlanService.addPlan(userId, request));
    }

    /**
     * 编辑计划
     */
    @ApiOperation("编辑计划")
    @PutMapping("/plan/{id}")
    @PreAuthorize("hasAuthority('checkin:plan:edit')")
    public Result<Boolean> update(@PathVariable Long id,
                                  @Validated @RequestBody CheckinPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        checkinPlanService.updatePlan(userId, id, request);
        return Result.success(true);
    }

    /**
     * 删除计划
     */
    @ApiOperation("删除计划")
    @DeleteMapping("/plan/{id}")
    @PreAuthorize("hasAuthority('checkin:plan:delete')")
    public Result<Boolean> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        checkinPlanService.deletePlan(userId, id);
        return Result.success(true);
    }

    /**
     * 打卡/取消打卡切换
     */
    @ApiOperation("打卡/取消打卡")
    @PostMapping("/record/toggle")
    @PreAuthorize("hasAuthority('checkin:record:toggle')")
    public Result<Map<String, Boolean>> toggle(
            @ApiParam("计划ID") @RequestParam Long planId,
            @ApiParam("打卡日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean checked = checkinRecordService.toggleRecord(userId, planId, date);
        Map<String, Boolean> data = new HashMap<>(1);
        data.put("checked", checked);
        return Result.success(data);
    }

    /**
     * 查询某月完成度概览
     */
    @ApiOperation("查询某月完成度概览")
    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('checkin:calendar:view')")
    public Result<List<CalendarDayResponse>> calendar(
            @ApiParam("月份(yyyy-MM)") @RequestParam String month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(checkinRecordService.getCalendar(userId, month));
    }
}
