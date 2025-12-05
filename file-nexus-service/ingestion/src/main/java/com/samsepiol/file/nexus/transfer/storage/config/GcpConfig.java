package com.samsepiol.file.nexus.transfer.storage.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GcpConfig {
    private String projectId;

    @Builder.Default
    private String region = "asia-south1";
}
