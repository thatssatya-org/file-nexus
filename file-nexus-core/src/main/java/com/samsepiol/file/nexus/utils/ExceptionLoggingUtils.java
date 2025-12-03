package com.samsepiol.file.nexus.utils;

import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.library.core.exception.LibraryException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ExceptionLoggingUtils {
    private static final String LOG_PATTERN = "[%s] : ";

    public static void logException(String logPrefix, FileNexusException exception) {
        if (exception.getHttpStatus().is4xxClientError()) {
            log.warn(String.format(LOG_PATTERN, logPrefix), exception);
        }
        log.error(String.format(LOG_PATTERN, logPrefix), exception);
    }

    public static void logException(String logPrefix, FileNexusRuntimeException exception) {
        if (exception.getHttpStatus().is4xxClientError()) {
            log.warn(String.format(LOG_PATTERN, logPrefix), exception);
        }
        log.error(String.format(LOG_PATTERN, logPrefix), exception);
    }

    public static void logException(String logPrefix, LibraryException exception) {
        log.error(String.format(LOG_PATTERN, logPrefix), exception);
    }

    public static void logException(String logPrefix, Exception exception) {
        log.error(String.format(LOG_PATTERN, logPrefix), exception);
    }
}
