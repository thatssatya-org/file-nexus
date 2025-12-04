package com.samsepiol.file.nexus.common.models.enums;

import com.samsepiol.message.queue.core.models.MessageHandlerType;

public enum FileNexusMessageHandler implements MessageHandlerType {
    FILE_CONTENTS,
    FILE_PULSE_STATUS
}
