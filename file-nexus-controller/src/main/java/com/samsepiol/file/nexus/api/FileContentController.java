package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.content.FileContentService;
import com.samsepiol.file.nexus.models.content.request.FileContentsQuery;
import com.samsepiol.file.nexus.models.content.response.FileContentsResponse;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.samsepiol.file.nexus.mapper.FileContentControllerMapper.MAPPER;

@RestController
@RequestMapping("/v1/files/{fileType}")
@RequiredArgsConstructor
public class FileContentController {

    private final FileContentService fileContentService;

    @PostMapping("/contents")
    public ResponseEntity<FileContentsResponse> getContents(@NotNull @Valid @PathVariable String fileType,
                                                            @NotNull @Valid @RequestBody FileContentsQuery query) {

        var serviceRequest = MAPPER
                .toFileContentFetchServiceRequest(fileType, DateTimeUtils.fromYYYYMMDD(query.getDate()), query);
        var fileContents = fileContentService.fetch(serviceRequest);
        var response = MAPPER.toFileContentsResponse(fileType, query, fileContents);
        return ResponseEntity.ok().body(response);
    }

}
