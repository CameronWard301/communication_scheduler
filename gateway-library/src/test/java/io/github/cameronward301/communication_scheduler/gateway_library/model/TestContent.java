package io.github.cameronward301.communication_scheduler.gateway_library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestContent implements Content {
    private String contentId;
    private String content;

    @Override
    public String getContentString() {
        return content;
    }

}
