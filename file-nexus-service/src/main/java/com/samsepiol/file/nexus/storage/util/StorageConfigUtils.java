package com.samsepiol.file.nexus.storage.util;

import com.samsepiol.file.nexus.storage.config.source.AbstractSourceConfig;
import com.samsepiol.file.nexus.storage.config.source.S3GcsSourceConfig;
import com.samsepiol.file.nexus.storage.config.source.SftpSourceConfig;

public class StorageConfigUtils {

    private StorageConfigUtils() {
        // Utility class
    }

    public static String getBucketName(AbstractSourceConfig sourceConfig) {
        if (sourceConfig == null) {
            throw new IllegalArgumentException("Source configuration cannot be null");
        }

        return switch (sourceConfig.getType()) {
            case S3, GCS -> {
                if (!(sourceConfig instanceof S3GcsSourceConfig s3GcpConfig)) {
                    throw new IllegalArgumentException("S3/GCP storage type requires S3GcpSourceConfig");
                }
                yield s3GcpConfig.getBucket();
            }
            case SFTP -> {
                if (!(sourceConfig instanceof SftpSourceConfig sftpConfig)) {
                    throw new IllegalArgumentException("SFTP storage type requires SftpSourceConfig");
                }
                String rootDir = sftpConfig.getRootDir();
                yield (rootDir != null && !rootDir.trim().isEmpty()) ? rootDir : sftpConfig.getName();
            }
        };
    }
}
