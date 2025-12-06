package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadata;
import com.samsepiol.file.nexus.metadata.message.handler.models.response.FileMetadatas;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataFetchServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/files/metadata")
@RequiredArgsConstructor
public class FileMetadataController {

    private final FileMetadataService metadataService;

    // TODO make api models
    @GetMapping("/{fileId}")
    public ResponseEntity<FileMetadata> getMetadata(@NotNull @Valid @PathVariable String fileId) {

        var response = metadataService.fetchMetadata(fileId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{fileType}")
    public ResponseEntity<FileMetadatas> getMetadatas(@NotBlank @PathVariable String fileType,
                                                      @NotBlank @RequestParam String date) {

        var response = metadataService.fetchMetadata(FileMetadataFetchServiceRequest.builder()
                .fileType(fileType)
                .date(date)
                .build());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fileId}/status")
    public ResponseEntity<Void> getContents(@NotNull @Valid @PathVariable String fileId,
                                            @NotNull @Valid @RequestParam MetadataStatus status) {

        metadataService.updateStatus(fileId, status);
        return ResponseEntity.ok().build();
    }

}
