package io.github.cameronward301.communication_scheduler.integration_tests.world;

import io.github.cameronward301.communication_scheduler.integration_tests.gateway.Gateway;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;


@Data
@Component
public class World {
    private HttpHeaders httpHeaders = new HttpHeaders();
    private HttpClientErrorException httpClientErrorException;
    private String workflowId;
    private String workflowRunId;
    private Gateway gateway;
    private String createdScheduleUserId;
}
