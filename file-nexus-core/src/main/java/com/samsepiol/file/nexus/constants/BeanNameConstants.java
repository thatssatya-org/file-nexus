package com.samsepiol.file.nexus.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanNameConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class MessageConsumers {
        public static final String FILE_CONTENTS_CONSUMER = "fileHandler";
        public static final String FILE_PULSE_STATUS_CONSUMER = "filePulseStatusHandler";
    }
}
