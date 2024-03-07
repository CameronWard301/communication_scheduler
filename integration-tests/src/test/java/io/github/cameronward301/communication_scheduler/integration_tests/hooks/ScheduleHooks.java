package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.ScheduleEntity;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
public class ScheduleHooks {
    private static boolean completedBeforeAll = false;
    private final ScheduleClient scheduleClient;
    private final List<ScheduleEntity> createdSchedules;
    private final ScheduleEntity scheduleEntity;
    List<String> scheduleIds = new ArrayList<>();

    public ScheduleHooks(ScheduleClient scheduleClient,
                         @Qualifier("scheduleEntities") List<ScheduleEntity> createdSchedules,
                         @Qualifier("scheduleEntity") ScheduleEntity scheduleEntity
    ) {
        this.scheduleClient = scheduleClient;
        this.createdSchedules = createdSchedules;
        this.scheduleEntity = scheduleEntity;
    }

    @Before("@RemoveExistingSchedules")
    public void removeExistingSchedulesBeforeAll() {
        if (!completedBeforeAll) {
            List<String> scheduleIds = new ArrayList<>();
            scheduleClient.listSchedules().filter(scheduleListDescription -> scheduleListDescription.getScheduleId().contains("integration-test")).forEach(schedule -> scheduleIds.add(schedule.getScheduleId()));
            for (String scheduleId : scheduleIds) {
                try {
                    scheduleClient.getHandle(scheduleId).delete();
                } catch (ScheduleException e) {
                    log.debug(e.getMessage());
                }
            }
            completedBeforeAll = true;
        }
    }

    @Before("@CreateMultipleSchedules")
    public void createSchedules() {
        for (ScheduleEntity schedule : createdSchedules) {
            String id = "integration-test-" + UUID.randomUUID();
            scheduleIds.add(id);
            scheduleClient.createSchedule(id, schedule.getSchedule(), schedule.getScheduleOptions()).describe();
        }
    }

    @Before("@CreateScheduleWithInterval")
    public void createSchedule() {
        scheduleClient.createSchedule(scheduleEntity.getScheduleId(), scheduleEntity.getSchedule(), scheduleEntity.getScheduleOptions());
    }

    @After("@RemoveMultipleSchedules")
    public void removeSchedules() {
        for (String scheduleId : scheduleIds) {
            try {
                scheduleClient.getHandle(scheduleId).delete();

            } catch (ScheduleException e) {
                log.debug(e.getMessage());
            }
        }
    }

    @After("@RemoveSchedule")
    public void deleteEntity() {
        scheduleClient.getHandle(scheduleEntity.getScheduleId()).delete();
    }


}
