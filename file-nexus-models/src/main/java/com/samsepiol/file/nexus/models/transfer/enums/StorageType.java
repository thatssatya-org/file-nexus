package com.samsepiol.file.nexus.models.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageType {
    S3("S3"),
    GCS("GCS"),
    SFTP("SFTP");

    private final String name;
}
