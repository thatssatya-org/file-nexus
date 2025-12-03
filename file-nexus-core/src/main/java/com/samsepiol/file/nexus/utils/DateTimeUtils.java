package com.samsepiol.file.nexus.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {
    private static final String ZONE = "Asia/Kolkata";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String DD_MM_YYYY_FORMAT = "ddMMyyyy";
    private static final ZoneId ZONE_ID = ZoneId.of(ZONE);

    private static final DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZONE_ID);
    private static final DateTimeFormatter DD_MM_YYYY_FORMATTER = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT).withZone(ZONE_ID);
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHoursMinutes(5, 30);

    public static @NonNull LocalDate fromYYYYMMDD(@NonNull String date) {
        return LocalDate.parse(date, YYYY_MM_DD_FORMATTER);
    }

    public static @NonNull LocalDate fromDDMMYYYY(@NonNull String date) {
        return LocalDate.parse(date, DD_MM_YYYY_FORMATTER);
    }

    public static @NonNull String toYYYYMMDD(@NonNull LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.of(ZONE)));
    }

    public static @NonNull LocalDate now() {
        return LocalDate.now(ZoneId.of(ZONE));
    }

    public static @NonNull Long currentTimeInEpoch() {
        return LocalDateTime.now().toEpochSecond(ZONE_OFFSET);
    }
}
