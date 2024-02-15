package io.github.cameronward301.communication_scheduler.schedule_api.model;

import io.temporal.client.schedules.ScheduleInfo;
import io.temporal.client.schedules.ScheduleSpec;
import io.temporal.client.schedules.ScheduleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ScheduleDescriptionDTO {
    private String id;
    private ScheduleInfo info;
    private ScheduleDTO schedule;
    private Map<String, List<?>> searchAttributes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private ScheduleState state;
        private ScheduleSpec spec;
    }
}
