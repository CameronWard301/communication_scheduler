package io.github.cameronward301.communication_scheduler.integration_tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.cameronward301.communication_scheduler.integration_tests.model.JwtDTO;
import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.ScheduleEntity;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
public class ScheduleHooks {
    private final ScheduleClient scheduleClient;
    private final List<ScheduleEntity> createdSchedules;
    private final ScheduleEntity scheduleEntity;
    private final World world;

    private final RestTemplate restTemplate;
    List<String> scheduleIds = new ArrayList<>();
    @Value("${schedule-api.address}")
    private String scheduleAPIURL;
    @Value("${auth-api.address}")
    private String authAPIUrl;

    public ScheduleHooks(ScheduleClient scheduleClient,
                         @Qualifier("scheduleEntities") List<ScheduleEntity> createdSchedules,
                         @Qualifier("scheduleEntity") ScheduleEntity scheduleEntity,
                         World world, RestTemplate restTemplate) {
        this.scheduleClient = scheduleClient;
        this.createdSchedules = createdSchedules;
        this.scheduleEntity = scheduleEntity;
        this.world = world;
        this.restTemplate = restTemplate;
    }

    @Before(value = "@RemoveExistingSchedules", order = 1)
    public void removeExistingSchedulesBeforeAll() {
        List<String> scheduleIds = new ArrayList<>();
        scheduleClient.listSchedules().filter(scheduleListDescription -> scheduleListDescription.getScheduleId().contains("integration-test")).forEach(schedule -> scheduleIds.add(schedule.getScheduleId()));
        for (String scheduleId : scheduleIds) {
            try {
                scheduleClient.getHandle(scheduleId).delete();
            } catch (ScheduleException e) {
                log.debug(e.getMessage());
            }
        }
    }

    @Before(value = "@CreateMultipleSchedules", order = 2)
    public void createSchedules() {
        for (ScheduleEntity schedule : createdSchedules) {
            String id = "integration-test-" + UUID.randomUUID();
            scheduleIds.add(id);
            scheduleClient.createSchedule(id, schedule.getSchedule(), schedule.getScheduleOptions()).describe();
        }
    }

    @Before(value = "@CreateScheduleWithInterval", order = 2)
    public void createSchedule() {
        scheduleClient.createSchedule(scheduleEntity.getScheduleId(), scheduleEntity.getSchedule(), scheduleEntity.getScheduleOptions());
        int attempts = 10;
        while (attempts > 0) {
            try {
                scheduleClient.getHandle(scheduleEntity.getScheduleId()).describe();
                break;
            } catch (ScheduleException e) {
                log.debug(e.getMessage());
                attempts--;
                if (attempts == 0) {
                    throw new RuntimeException("Schedule not created");
                }
            }
        }
    }

    @Before(value = "@CheckSchedulesAreCreated", order = 3)
    public void checkSchedulesAreCreated() {
        for (String scheduleId : scheduleIds) {
            int attempts = 10;
            while (attempts > 0) {
                try {
                    scheduleClient.getHandle(scheduleId).describe();
                    break;
                } catch (ScheduleException e) {
                    log.debug(e.getMessage());
                    attempts--;
                    if (attempts == 0) {
                        throw new RuntimeException("Schedule not created");
                    }
                }
            }
        }
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

    @After("@RemoveScheduleByUserId")
    public void removeScheduleByUserId() {
        //send a delete request with the userId param set:
        HttpHeaders httpHeaders = world.getHttpHeaders();
        String authToken = Objects.requireNonNull(restTemplate.postForEntity(authAPIUrl, List.of("SCHEDULE:DELETE"), JwtDTO.class).getBody()).getToken();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + authToken);
        restTemplate.exchange(scheduleAPIURL + "?userId=" + world.getCreatedScheduleUserId(), HttpMethod.DELETE, new HttpEntity<>(world.getHttpHeaders()), String.class);
    }

    @After("@RemoveSchedule")
    public void deleteEntity() {
        scheduleClient.getHandle(scheduleEntity.getScheduleId()).delete();
    }


}
