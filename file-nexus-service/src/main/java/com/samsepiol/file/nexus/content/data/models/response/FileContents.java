package com.samsepiol.file.nexus.content.data.models.response;

import com.samsepiol.file.nexus.content.data.models.FileContent;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class FileContents {

    @Builder.Default
    @NonNull
    List<FileContent> contents = Collections.emptyList();

    public static FileContents empty() {
        return FileContents.builder().build();
    }
}
