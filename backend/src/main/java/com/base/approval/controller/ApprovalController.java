package com.base.approval.controller;

import com.base.approval.dto.ApprovalInstanceResponse;
import com.base.approval.dto.ApprovalSubmitRequest;
import com.base.approval.service.ApprovalInstanceService;
import com.base.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 审批实例控制器
 *
 * @author base
 */
@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
@Api(tags = "审批管理")
public class ApprovalController {

    private final ApprovalInstanceService approvalInstanceService;

    @PostMapping("/submit")
    @ApiOperation("发起审批")
    public Result<ApprovalInstanceResponse> submit(@Validated @RequestBody ApprovalSubmitRequest request) {
        return Result.success(approvalInstanceService.submitApproval(request));
    }

    @PostMapping("/{id}/cancel")
    @ApiOperation("撤销审批")
    public Result<Void> cancel(@PathVariable Long id) {
        approvalInstanceService.cancelApproval(id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("分页查询审批实例")
    public Result<IPage<ApprovalInstanceResponse>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("审批状态") @RequestParam(required = false) String status,
            @ApiParam("业务类型") @RequestParam(required = false) String businessType,
            @ApiParam("发起人用户ID") @RequestParam(required = false) Long applicantUserId) {
        return Result.success(approvalInstanceService.pageList(pageNum, pageSize, status, businessType, applicantUserId));
    }

    @GetMapping("/{id}")
    @ApiOperation("审批详情")
    public Result<ApprovalInstanceResponse> detail(@PathVariable Long id) {
        return Result.success(approvalInstanceService.getDetail(id));
    }

    @GetMapping("/business/{businessKey}")
    @ApiOperation("按业务键查询")
    public Result<ApprovalInstanceResponse> getByBusinessKey(@PathVariable String businessKey) {
        return Result.success(approvalInstanceService.getByBusinessKey(businessKey));
    }

    @PostMapping("/{id}/sync")
    @ApiOperation("手动同步审批状态")
    public Result<Void> sync(@PathVariable Long id) {
        approvalInstanceService.syncApprovalStatus(id);
        return Result.success();
    }
}
