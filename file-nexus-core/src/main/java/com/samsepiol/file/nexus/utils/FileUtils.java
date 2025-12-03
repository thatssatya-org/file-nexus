package com.samsepiol.file.nexus.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    public static @NonNull String getFileId(@NonNull String fileType, @NonNull LocalDate date, String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return String.format("%s-%s", fileType, DateTimeUtils.toYYYYMMDD(date));
        }
        return String.format("%s-%s-%s", fileType, DateTimeUtils.toYYYYMMDD(date), suffix);
    }

    public static @NonNull String getFileNameWithoutExtension(@NonNull String fileName) {
        int dotSeparatorIndex = fileName.lastIndexOf('.');
        if (dotSeparatorIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotSeparatorIndex);
    }
}
