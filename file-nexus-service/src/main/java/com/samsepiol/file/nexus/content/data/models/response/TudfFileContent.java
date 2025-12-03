package com.samsepiol.file.nexus.content.data.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TudfFileContent {

    @JsonProperty("payload")
    private Payload payload;

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class Payload{
        private String message;
        private String rowNumber;
    }
}
