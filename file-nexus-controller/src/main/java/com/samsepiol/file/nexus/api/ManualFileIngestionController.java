package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.content.message.handler.FileContentConsumerService;
import com.samsepiol.file.nexus.content.message.handler.models.request.ByteArrayFileContentHandlerServiceRequest;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import com.samsepiol.file.nexus.utils.FilePulseConnectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/files/manual")
@RequiredArgsConstructor
public class ManualFileIngestionController {
    private final FileContentConsumerService fileContentConsumerService;

    @PostMapping
    public ResponseEntity<Void> ingest(@RequestPart MultipartFile file) throws IOException {
        var serviceRequest = ByteArrayFileContentHandlerServiceRequest.builder()
                .message(file.getBytes())
                .metadata(Map.of(FilePulseConnectorUtils.FILE_NAME_HEADER, Objects.requireNonNullElse(file.getOriginalFilename(), file.getName()),
                        FilePulseConnectorUtils.FILE_LAST_MODIFIED_AT_HEADER, DateTimeUtils.currentTimeInEpoch().toString()))
                .build();
        fileContentConsumerService.handle(serviceRequest);
        return ResponseEntity.ok().build();
    }

}
