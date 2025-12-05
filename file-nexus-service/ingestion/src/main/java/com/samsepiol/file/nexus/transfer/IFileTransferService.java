package com.samsepiol.file.nexus.transfer;

import com.samsepiol.file.nexus.models.transfer.response.StoreConnectivityResponse;
import com.samsepiol.file.nexus.transfer.models.request.FileTransferServiceRequest;

public interface IFileTransferService {

    String transferFile(FileTransferServiceRequest request);
    StoreConnectivityResponse checkStoreConnectivity(String storeName, String bucketName);
}
