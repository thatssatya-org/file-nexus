package com.samsepiol.file.nexus.content.data.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FileContent {
    String id;
    String fileId;
    String rowNumber;
    String content;
}
