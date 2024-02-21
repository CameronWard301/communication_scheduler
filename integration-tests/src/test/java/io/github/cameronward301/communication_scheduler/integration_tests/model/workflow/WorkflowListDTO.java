package io.github.cameronward301.communication_scheduler.integration_tests.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.ScheduleListDescriptionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WorkflowListDTO {
    private List<WorkflowExecutionDTO> content;
    private WorkflowListDTO.Pageable pageable;
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int size;
    private int number;
    private WorkflowListDTO.Sort sort;
    private boolean first;
    private int numberOfElements;
    private boolean empty;

    public WorkflowListDTO(
            @JsonProperty("content") List<WorkflowExecutionDTO> content, @JsonProperty("number") int number,
            @JsonProperty("size") int size, @JsonProperty("totalElements") int totalElements,
            @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort,
            @JsonProperty("numberOfElements") int numberOfElements
    ) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.last = last;
        this.totalPages = totalPages;
        this.numberOfElements = numberOfElements;
    }

    @Data
    public static class Pageable {
        private int pageNumber;
        private int pageSize;
        private ScheduleListDescriptionDTO.Sort sort;
        private int offset;
        private boolean paged;
        private boolean unpaged;
    }

    @Data
    public static class Sort {
        private boolean sorted;
        private boolean empty;
        private boolean unsorted;
    }
}
