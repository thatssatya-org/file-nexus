package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.content.data.models.response.FileContents;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.content.models.request.FileContentFetchServiceRequest;
import com.samsepiol.file.nexus.content.models.request.FileContentSaveServiceRequest;
import lombok.NonNull;

/**
 * Manages read and write of file contents
 *
 * @author satyajitroy
 */
public interface FileContentService {

    @NonNull
    FileContents fetch(@NonNull FileContentFetchServiceRequest request);

    @NonNull
    FileContents save(@NonNull FileContentSaveServiceRequest request) throws UnsupportedFileException;
}
