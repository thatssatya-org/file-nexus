package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.models.transfer.request.FileTransferRequest;
import com.samsepiol.file.nexus.models.transfer.response.FileTransferResponse;
import com.samsepiol.file.nexus.models.transfer.response.StoreConnectivityResponse;
import com.samsepiol.file.nexus.transfer.IFileTransferService;
import com.samsepiol.file.nexus.transfer.mapper.FileTransferAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/file-transfer")
@RequiredArgsConstructor
public class FileTransferController {

    private final IFileTransferService fileTransferService;


    @PostMapping
    public ResponseEntity<FileTransferResponse> transferFile(@Valid @RequestBody FileTransferRequest request){
        String response = fileTransferService.transferFile(FileTransferAdapter.from(request));
        return ResponseEntity.ok(FileTransferResponse.builder().workflowId(response).build());
    }

    @GetMapping("/check-connectivity")
    public ResponseEntity<StoreConnectivityResponse> checkConnectivity(@Valid @NotBlank @RequestParam("sourceName") String sourceName,@Valid @NotBlank @RequestParam("bucketName") String bucketName){
        return ResponseEntity.ok(fileTransferService.checkStoreConnectivity(sourceName, bucketName));
    }

}
