package com.samsepiol.file.nexus.temporal.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdGeneratorUtil {

    public static String generateMetadataStatusWorkflowId(String fileId) {
        return "MS-".concat(fileId);
    }
}
