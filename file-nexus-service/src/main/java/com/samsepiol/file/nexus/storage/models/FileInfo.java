package com.samsepiol.file.nexus.storage.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents file information from storage systems.
 * Contains metadata needed for processing decisions.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fileKey;
    /**
     * The full path of the file in the storage system.
     */
    private String filePath;

    /**
     * The last modified timestamp of the file.
     */
    private Instant lastModified;

    /**
     * The size of the file in bytes.
     */
    private long size;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(filePath, fileInfo.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(filePath);
    }
}
