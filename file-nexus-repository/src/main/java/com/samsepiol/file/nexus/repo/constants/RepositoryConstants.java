package com.samsepiol.file.nexus.repo.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RepositoryConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class FileContentEntityConstants {

        public static final String FILE_ID = "fileId";
        public static final String INDEX_1 = "index1";
        public static final String INDEX_2 = "index2";
        public static final String INDEX_3 = "index3";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class FileMetadataEntityConstants {
        public static final String FILE_TYPE = "fileType";
        public static final String DATE = "date";
    }
}
