package com.base.checkin;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.base.checkin.dto.CheckinPlanResponse;
import com.base.checkin.entity.CheckinPlan;
import com.base.checkin.entity.CheckinRecord;
import com.base.checkin.mapper.CheckinPlanMapper;
import com.base.checkin.mapper.CheckinRecordMapper;
import com.base.checkin.service.impl.CheckinPlanServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 打卡计划服务测试
 */
@ExtendWith(MockitoExtension.class)
class CheckinPlanServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CheckinPlanMapper checkinPlanMapper;

    @Mock
    private CheckinRecordMapper checkinRecordMapper;

    private CheckinPlanServiceImpl checkinPlanService;

    @BeforeAll
    static void initializeTableInfo() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "checkin-plan-test");
        assistant.setCurrentNamespace(CheckinPlanServiceImplTest.class.getName());
        TableInfoHelper.initTableInfo(assistant, CheckinRecord.class);
    }

    @BeforeEach
    void setUp() {
        checkinPlanService = new CheckinPlanServiceImpl(checkinPlanMapper, checkinRecordMapper);
    }

    @Test
    void listByUserIdAndDateShouldIncludeDeletedPlanInHistoryDate() {
        // 计划A：7-1创建，7-10删除；计划B：7-5创建未删除
        CheckinPlan deletedPlan = createPlan(10L, "计划A", 0, LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10));
        CheckinPlan activePlan = createPlan(11L, "计划B", 0, LocalDate.of(2026, 7, 5), null);
        when(checkinPlanMapper.selectEnabledByUserIdIncludeDeleted(eq(USER_ID)))
                .thenReturn(Arrays.asList(deletedPlan, activePlan));
        // 7-8 计划A已打卡
        CheckinRecord record = createRecord(10L, LocalDate.of(2026, 7, 8));
        when(checkinRecordMapper.selectList(any())).thenReturn(Collections.singletonList(record));

        List<CheckinPlanResponse> result = checkinPlanService.listByUserIdAndDate(USER_ID, LocalDate.of(2026, 7, 8));

        assertEquals(2, result.size());
        CheckinPlanResponse deletedResp = result.get(0);
        assertEquals(10L, deletedResp.getId());
        assertTrue(deletedResp.getDeleted());
        assertTrue(deletedResp.getTodayChecked());
        CheckinPlanResponse activeResp = result.get(1);
        assertEquals(11L, activeResp.getId());
        assertFalse(activeResp.getDeleted());
        assertFalse(activeResp.getTodayChecked());
    }

    @Test
    void listByUserIdAndDateShouldExcludeDeletedPlanOnDeleteDate() {
        // 计划A：7-1创建，7-10删除，删除日不计入
        CheckinPlan deletedPlan = createPlan(10L, "计划A", 0, LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10));
        when(checkinPlanMapper.selectEnabledByUserIdIncludeDeleted(eq(USER_ID)))
                .thenReturn(Collections.singletonList(deletedPlan));

        List<CheckinPlanResponse> result = checkinPlanService.listByUserIdAndDate(USER_ID, LocalDate.of(2026, 7, 10));

        assertTrue(result.isEmpty());
    }

    @Test
    void listByUserIdAndDateShouldExcludePlanBeforeCreateDate() {
        // 计划A：7-5创建，创建前不生效
        CheckinPlan plan = createPlan(10L, "计划A", 0, LocalDate.of(2026, 7, 5), null);
        when(checkinPlanMapper.selectEnabledByUserIdIncludeDeleted(eq(USER_ID)))
                .thenReturn(Collections.singletonList(plan));

        List<CheckinPlanResponse> result = checkinPlanService.listByUserIdAndDate(USER_ID, LocalDate.of(2026, 7, 3));

        assertTrue(result.isEmpty());
    }

    private CheckinPlan createPlan(Long planId, String title, int planType, LocalDate createdDate, LocalDate deletedDate) {
        CheckinPlan plan = new CheckinPlan();
        plan.setId(planId);
        plan.setUserId(USER_ID);
        plan.setTitle(title);
        plan.setPlanType(planType);
        plan.setStatus(1);
        plan.setSortOrder(0);
        plan.setCreateTime(createdDate.atStartOfDay());
        if (deletedDate == null) {
            plan.setDeleted(0);
            return plan;
        }
        plan.setDeleted(1);
        plan.setDeletedTime(deletedDate.atTime(12, 0));
        return plan;
    }

    private CheckinRecord createRecord(Long planId, LocalDate date) {
        CheckinRecord record = new CheckinRecord();
        record.setPlanId(planId);
        record.setUserId(USER_ID);
        record.setCheckinDate(date);
        record.setDeleted(0);
        return record;
    }
}
