package com.samsepiol.file.nexus.ingestion.workflow.dto;

import com.samsepiol.file.nexus.storage.models.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Request DTO for the file ingestion workflow.
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class FileIngestionWorkflowRequest {

    /**
     * The source storage configuration containing all necessary details
     * for connecting to and accessing the source storage.
     */
    private String sourceName;

    /**
     * The path of the file in the source storage.
     */
    private String filePath;

    /**
     * The list of destination configurations.
     */
    private List<String> destinations;
    /**
     * The file information containing metadata about the file being ingested.
     */
    private FileInfo fileInfo;
}
