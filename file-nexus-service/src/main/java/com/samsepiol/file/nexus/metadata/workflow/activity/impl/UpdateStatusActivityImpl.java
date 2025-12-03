package com.samsepiol.file.nexus.metadata.workflow.activity.impl;

import com.samsepiol.file.nexus.metadata.FileMetadataService;
import com.samsepiol.file.nexus.metadata.models.request.FileMetadataSaveRequest;
import com.samsepiol.file.nexus.metadata.workflow.activity.UpdateStatusActivity;
import com.samsepiol.file.nexus.metadata.workflow.activity.request.UpdateMetadataStatusActivityRequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Slf4j
@Component
public class UpdateStatusActivityImpl implements UpdateStatusActivity {
    private final FileMetadataService fileHandlerDataService;

    @Override
    public void execute(@NonNull UpdateMetadataStatusActivityRequest request) {
        log.info("[UpdateStatusActivity] Updating File metadata status to COMPLETED for fileId: {}, fileName: {}",
                request.getParsedFileMetaData().getFileId(), request.getParsedFileMetaData().getName());

        var saveRequest = FileMetadataSaveRequest.forCompleted(request.getParsedFileMetaData());
        fileHandlerDataService.saveOrUpdate(saveRequest);
    }
}
