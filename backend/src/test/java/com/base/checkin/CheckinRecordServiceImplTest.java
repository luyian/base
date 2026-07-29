package com.base.checkin;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.base.checkin.dto.CalendarDayResponse;
import com.base.checkin.entity.CheckinPlan;
import com.base.checkin.entity.CheckinRecord;
import com.base.checkin.mapper.CheckinPlanMapper;
import com.base.checkin.mapper.CheckinRecordMapper;
import com.base.checkin.service.impl.CheckinRecordServiceImpl;
import com.base.common.exception.BusinessException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 打卡记录服务测试
 */
@ExtendWith(MockitoExtension.class)
class CheckinRecordServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long PLAN_ID = 10L;

    @Mock
    private CheckinRecordMapper checkinRecordMapper;

    @Mock
    private CheckinPlanMapper checkinPlanMapper;

    private CheckinRecordServiceImpl checkinRecordService;

    @BeforeAll
    static void initializeTableInfo() {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "checkin-test");
        assistant.setCurrentNamespace(CheckinRecordServiceImplTest.class.getName());
        TableInfoHelper.initTableInfo(assistant, CheckinRecord.class);
    }

    @BeforeEach
    void setUp() {
        checkinRecordService = new CheckinRecordServiceImpl(checkinRecordMapper, checkinPlanMapper);
    }

    @Test
    void toggleRecordShouldUseAtomicToggleAndReturnCheckedState() {
        LocalDate date = LocalDate.of(2026, 7, 23);
        CheckinPlan plan = createPlan(PLAN_ID, USER_ID, LocalDate.of(2026, 7, 1), null);
        CheckinRecord record = createRecord(PLAN_ID, date, 0);
        when(checkinPlanMapper.selectByIdForUpdate(PLAN_ID)).thenReturn(plan);
        when(checkinRecordMapper.selectByPlanAndDateIncludeDeleted(PLAN_ID, date)).thenReturn(record);

        boolean checked = checkinRecordService.toggleRecord(USER_ID, PLAN_ID, date);

        assertTrue(checked);
        verify(checkinRecordMapper).atomicToggle(PLAN_ID, USER_ID, date);
    }

    @Test
    void toggleRecordShouldReturnUncheckedStateAfterCancellation() {
        LocalDate date = LocalDate.of(2026, 7, 23);
        CheckinPlan plan = createPlan(PLAN_ID, USER_ID, LocalDate.of(2026, 7, 1), null);
        CheckinRecord record = createRecord(PLAN_ID, date, 1);
        when(checkinPlanMapper.selectByIdForUpdate(PLAN_ID)).thenReturn(plan);
        when(checkinRecordMapper.selectByPlanAndDateIncludeDeleted(PLAN_ID, date)).thenReturn(record);

        boolean checked = checkinRecordService.toggleRecord(USER_ID, PLAN_ID, date);

        assertFalse(checked);
        verify(checkinRecordMapper).atomicToggle(PLAN_ID, USER_ID, date);
    }

    @Test
    void toggleRecordShouldRejectPlanOwnedByAnotherUser() {
        CheckinPlan plan = createPlan(PLAN_ID, 2L, LocalDate.of(2026, 7, 1), null);
        when(checkinPlanMapper.selectByIdForUpdate(PLAN_ID)).thenReturn(plan);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> checkinRecordService.toggleRecord(USER_ID, PLAN_ID, LocalDate.of(2026, 7, 23)));

        assertEquals(403, exception.getCode());
    }

    @Test
    void getCalendarShouldUsePlanEffectiveDatesForDailyTotal() {
        CheckinPlan deletedPlan = createPlan(10L, USER_ID, LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 10));
        CheckinPlan activePlan = createPlan(11L, USER_ID, LocalDate.of(2026, 7, 5), null);
        List<CheckinRecord> records = Arrays.asList(
                createRecord(10L, LocalDate.of(2026, 7, 3), 0),
                createRecord(10L, LocalDate.of(2026, 7, 6), 0),
                createRecord(11L, LocalDate.of(2026, 7, 6), 0),
                createRecord(11L, LocalDate.of(2026, 7, 11), 0));
        when(checkinPlanMapper.selectEnabledByUserIdIncludeDeleted(USER_ID))
                .thenReturn(Arrays.asList(deletedPlan, activePlan));
        when(checkinRecordMapper.selectList(any())).thenReturn(records);

        List<CalendarDayResponse> calendar = checkinRecordService.getCalendar(USER_ID, "2026-07");

        assertEquals(1, day(calendar, 1).getTotal());
        assertEquals(1, day(calendar, 4).getTotal());
        assertEquals(2, day(calendar, 5).getTotal());
        assertEquals(2, day(calendar, 9).getTotal());
        assertEquals(1, day(calendar, 10).getTotal());
        assertEquals(1, day(calendar, 11).getTotal());
        assertEquals(2, day(calendar, 6).getCompleted());
        assertEquals(1, day(calendar, 11).getCompleted());
    }

    @Test
    void getCalendarShouldRejectInvalidMonth() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> checkinRecordService.getCalendar(USER_ID, "2026-13"));

        assertEquals(400, exception.getCode());
    }

    private CalendarDayResponse day(List<CalendarDayResponse> calendar, int day) {
        return calendar.get(day - 1);
    }

    private CheckinPlan createPlan(Long planId, Long userId, LocalDate createdDate, LocalDate deletedDate) {
        CheckinPlan plan = new CheckinPlan();
        plan.setId(planId);
        plan.setUserId(userId);
        plan.setStatus(1);
        plan.setCreateTime(createdDate.atStartOfDay());
        if (deletedDate == null) {
            plan.setDeleted(0);
            return plan;
        }
        plan.setDeleted(1);
        plan.setDeletedTime(deletedDate.atTime(12, 0));
        return plan;
    }

    private CheckinRecord createRecord(Long planId, LocalDate date, int deleted) {
        CheckinRecord record = new CheckinRecord();
        record.setPlanId(planId);
        record.setUserId(USER_ID);
        record.setCheckinDate(date);
        record.setDeleted(deleted);
        return record;
    }
}
