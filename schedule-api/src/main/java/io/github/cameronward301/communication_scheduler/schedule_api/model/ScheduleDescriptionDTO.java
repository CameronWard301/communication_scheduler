package io.github.cameronward301.communication_scheduler.schedule_api.model;

import io.temporal.client.schedules.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScheduleDescriptionDTO {
    private String id;
    private ScheduleInfo info;
    private ScheduleDTO schedule;
    private Map<String, List<?>> searchAttributes;

    @Data
    public static class ScheduleDTO {
        private ScheduleState state;
        private ScheduleSpec spec;
    }
}
