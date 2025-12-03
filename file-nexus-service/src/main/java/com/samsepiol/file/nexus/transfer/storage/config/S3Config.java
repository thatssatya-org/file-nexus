package com.samsepiol.file.nexus.transfer.storage.config;

import com.amazonaws.regions.Regions;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class S3Config {

    @Builder.Default
    private String region = Regions.AP_SOUTH_1.getName();
}
