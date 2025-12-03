package com.samsepiol.file.nexus.content.data;


import com.samsepiol.file.nexus.content.data.models.request.FileContentFetchRequest;
import com.samsepiol.file.nexus.content.data.models.request.FileContentSaveRequest;
import com.samsepiol.file.nexus.content.data.models.response.FileContents;

/**
 * Wrapper service over repository to encapsulate business need of file contents read and write
 * @author satyajitroy
 */
public interface FileContentDataService {

    FileContents save(FileContentSaveRequest request);

    FileContents fetch(FileContentFetchRequest request);
}
