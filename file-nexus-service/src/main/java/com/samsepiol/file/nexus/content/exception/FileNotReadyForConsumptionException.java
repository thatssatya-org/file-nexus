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
public class FileNotReadyForConsumptionException extends FileNexusRuntimeException {
    @NonNull
    String fileType;
    @NonNull
    LocalDate date;

    @Builder(access = AccessLevel.PRIVATE)
    private FileNotReadyForConsumptionException(@NonNull String fileType, @NonNull LocalDate date) {
        super(Error.FILE_NOT_READY_FOR_CONSUMPTION_ERROR);
        this.fileType = fileType;
        this.date = date;
    }

    public static FileNotReadyForConsumptionException create(@NonNull String fileType,
                                                             @NonNull LocalDate date) {
        return FileNotReadyForConsumptionException.builder()
                .fileType(fileType)
                .date(date)
                .build();
    }
}
