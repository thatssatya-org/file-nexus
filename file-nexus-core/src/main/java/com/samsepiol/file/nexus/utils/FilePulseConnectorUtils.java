package com.samsepiol.file.nexus.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilePulseConnectorUtils {
    private static final String FILE_NAME_HEADER = "connect.file.name";
    private static final String FILE_LAST_MODIFIED_AT_HEADER = "connect.file.lastModified";

    public static @NonNull Optional<String> getFileNameFromHeaders(Map<String, String> headers) {
        return Optional.ofNullable(headers.get(FILE_NAME_HEADER));
    }

    public static @NonNull Optional<LocalDate> getDateFromFileNameContainedAsDDMMYYYY(@NonNull String fileName,
                                                                                      @NonNull String separator,
                                                                                      @NonNull Integer index) {
        var parts = getFileNameParts(fileName, separator);

        if (index >= parts.size()) {
            return Optional.empty();
        }

        return Optional.of(DateTimeUtils.fromDDMMYYYY(parts.get(index)));
    }

    public static @NonNull Optional<LocalDate> getDateFromFileNameContainedAsYYYYMMDD(@NonNull String fileName,
                                                                                      @NonNull String separator,
                                                                                      @NonNull Integer index) {
        var parts = getFileNameParts(fileName, separator);

        if (index >= parts.size()) {
            return Optional.empty();
        }

        return Optional.of(DateTimeUtils.fromYYYYMMDD(parts.get(index)));
    }

    private static List<String> getFileNameParts(String fileName, String separator) {
        String fileNameWithoutExtension = FileUtils.getFileNameWithoutExtension(fileName);

        return Arrays.stream(fileNameWithoutExtension.split(separator)).toList();
    }


    public static Long getCreatedAt(Map<String, String> headers) {
        return Long.valueOf(headers.get(FILE_LAST_MODIFIED_AT_HEADER));
    }

}
