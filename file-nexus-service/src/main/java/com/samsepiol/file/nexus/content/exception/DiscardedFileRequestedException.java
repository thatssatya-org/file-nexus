package com.samsepiol.file.nexus.content.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Value
public class DiscardedFileRequestedException extends FileNexusRuntimeException {
    @NonNull
    String fileType;
    @NonNull
    LocalDate date;

    @Builder(access = AccessLevel.PRIVATE)
    private DiscardedFileRequestedException(@NonNull String fileType, @NonNull LocalDate date) {
        super(Error.DISCARDED_FILE_REQUESTED_ERROR);
        this.fileType = fileType;
        this.date = date;
    }

    public static DiscardedFileRequestedException create(@NonNull String fileType,
                                                         @NonNull LocalDate date) {
        return DiscardedFileRequestedException.builder()
                .fileType(fileType)
                .date(date)
                .build();
    }
}
