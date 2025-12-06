package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.models.response.FileMetadata;
import com.samsepiol.file.nexus.metadata.models.response.FileMetadatas;
import com.samsepiol.file.nexus.metadata.parser.models.response.ParsedFileMetaData;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;

import java.util.ArrayList;
import java.util.List;

public class MetadataTestUtil {

    public static MetadataEntity createTestMetadataEntity(){
        return MetadataEntity.builder()
                .id("STATEMENT_20241007")
                .fileType("STATEMENT")
                .fileName("STATEMENT_20241007")
                .createdAt(124L)
                .updatedAt(456L)
                .date("20241007")
                .status(MetadataStatus.PENDING)
                .build();
    }

    public static FileMetadata createTestFileMetadata(){
        return FileMetadata.builder()
                .id("STATEMENT_20241007")
                .fileType("STATEMENT")
                .date("20241007")
                .status(MetadataStatus.PENDING)
                .build();
    }

    public static FileMetadatas createTestFileMetadatas(){
        List<FileMetadata> fileMetadataList = new ArrayList<>();
        fileMetadataList.add(createTestFileMetadata());
        fileMetadataList.add(createTestFileMetadata());
        return FileMetadatas.from(fileMetadataList);
    }

    public static List<MetadataEntity> createTestMetadataEntityList(){
        List<MetadataEntity> metadataEntityList = new ArrayList<>();
        metadataEntityList.add(createTestMetadataEntity());
        metadataEntityList.add(createTestMetadataEntity());
        return metadataEntityList;
    }

    public static ParsedFileMetaData createTestParsedFileMetadata(){
        return ParsedFileMetaData.builder()
                .fileId("STATEMENT_20241007")
                .name("FileName")
                .fileType("STATEMENT")
                .fileDate("20241007")
                .createdAt(124L)
                .build();
    }
}
