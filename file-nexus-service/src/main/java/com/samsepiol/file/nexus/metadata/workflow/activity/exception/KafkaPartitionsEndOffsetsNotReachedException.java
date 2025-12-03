package com.samsepiol.file.nexus.metadata.workflow.activity.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class KafkaPartitionsEndOffsetsNotReachedException extends FileNexusRuntimeException {

    @Builder(access = AccessLevel.PRIVATE)
    private KafkaPartitionsEndOffsetsNotReachedException() {
        super(Error.KAFKA_PARTITIONS_END_OFFSETS_AND_COMMITED_OFFSETS_MISMATCH);
    }

    public static KafkaPartitionsEndOffsetsNotReachedException create() {
        return KafkaPartitionsEndOffsetsNotReachedException.builder().build();
    }
}
