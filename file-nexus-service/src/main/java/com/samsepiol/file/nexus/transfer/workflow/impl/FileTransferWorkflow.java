package com.samsepiol.file.nexus.transfer.workflow.impl;

import com.samsepiol.file.nexus.transfer.workflow.IFileTransferWorkflow;
import com.samsepiol.file.nexus.transfer.workflow.activites.IArchiveFileActivity;
import com.samsepiol.file.nexus.transfer.workflow.activites.IFetchFileNamesActivity;
import com.samsepiol.file.nexus.transfer.workflow.activites.ISendEventActivity;
import com.samsepiol.file.nexus.transfer.workflow.activites.IUploadFileActivity;
import com.samsepiol.file.nexus.transfer.workflow.activites.request.ArchiveFileActivityRequest;
import com.samsepiol.file.nexus.transfer.workflow.activites.request.UploadFileActivityRequest;
import com.samsepiol.file.nexus.transfer.workflow.dto.FileTransferEventPayload;
import com.samsepiol.file.nexus.transfer.workflow.dto.FileTransferWorkflowRequest;
import com.samsepiol.helper.utils.CommonSerializationUtil;
import com.samsepiol.temporal.annotations.TemporalWorkflow;
import io.temporal.workflow.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeUtils;

import java.util.List;

import static com.samsepiol.file.nexus.transfer.constants.FileTransferWorkflowConstants.FILE_TRANSFER_WORKFLOW_COMPLETED;
import static com.samsepiol.file.nexus.transfer.constants.FileTransferWorkflowConstants.FILE_TRANSFER_WORKFLOW_INITIATED;

@Slf4j
@RequiredArgsConstructor
@TemporalWorkflow
public class FileTransferWorkflow implements IFileTransferWorkflow {
    private final IFetchFileNamesActivity fetchFileNamesActivity = IFetchFileNamesActivity.create();
    private final IUploadFileActivity uploadFileActivity = IUploadFileActivity.create();
    private final IArchiveFileActivity archiveFileActivity = IArchiveFileActivity.create();
    private final ISendEventActivity sendEventActivity = ISendEventActivity.create();

    @Override
    public void transferFile(FileTransferWorkflowRequest request) {
        Long startTime = Workflow.sideEffect(Long.class, DateTimeUtils::currentTimeMillis);
        FileTransferEventPayload eventPayload = FileTransferEventPayload.builder().initiatedAt(startTime).source(request.getSourceStore()).target(request.getTargetStore()).build();
        sendEventActivity.sendEvent(FILE_TRANSFER_WORKFLOW_INITIATED, CommonSerializationUtil.convertObjectToMap(eventPayload));
        List<String> remoteFilePaths = fetchFileNamesActivity.getFileNames(request.getSourceStore(), request.getTargetStore());

        for (String remoteFilePath : remoteFilePaths) {
            uploadFileActivity.upload(createUploadActivityRequest(request, remoteFilePath));
            archiveFileActivity.archive(createArchiveFileActivityRequest(request, remoteFilePath));
        }
        Long endTime = Workflow.sideEffect(Long.class, DateTimeUtils::currentTimeMillis);
        FileTransferEventPayload completionEvent = FileTransferEventPayload.builder().completedAt(endTime).source(request.getSourceStore()).target(request.getTargetStore()).build();
        sendEventActivity.sendEvent(FILE_TRANSFER_WORKFLOW_COMPLETED, CommonSerializationUtil.convertObjectToMap(completionEvent));
    }

    private UploadFileActivityRequest createUploadActivityRequest(FileTransferWorkflowRequest request, String remoteFilePath){
        return UploadFileActivityRequest.builder()
                .remoteFilePath(remoteFilePath)
                .sourceStore(request.getSourceStore())
                .targetStore(request.getTargetStore())
                .build();
    }
    private ArchiveFileActivityRequest createArchiveFileActivityRequest(FileTransferWorkflowRequest request, String remoteFilePath){
        return ArchiveFileActivityRequest.builder()
                .remoteFilePath(remoteFilePath)
                .sourceStore(request.getSourceStore())
                .targetStore(request.getTargetStore())
                .build();
    }
}
