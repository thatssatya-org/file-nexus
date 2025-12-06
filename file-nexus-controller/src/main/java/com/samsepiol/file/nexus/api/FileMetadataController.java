package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.models.content.response.FileContentsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{fileId}/status")
    public ResponseEntity<FileContentsResponse> getContents(@NotNull @Valid @PathVariable String fileId,
                                                            @NotNull @Valid @RequestParam MetadataStatus status) {

        metadataService.updateStatus(fileId, status);
        return ResponseEntity.ok().build();
    }

}
