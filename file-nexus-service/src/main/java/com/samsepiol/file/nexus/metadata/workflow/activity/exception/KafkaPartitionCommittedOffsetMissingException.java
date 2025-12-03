package com.samsepiol.file.nexus.metadata.workflow.activity.exception;

import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.enums.Error;
import lombok.AccessLevel;
import lombok.Builder;

public class KafkaPartitionCommittedOffsetMissingException extends FileNexusRuntimeException {

    @Builder(access = AccessLevel.PRIVATE)
    private KafkaPartitionCommittedOffsetMissingException() {
        super(Error.KAFKA_PARTITION_COMMITTED_OFFSET_MISSING);
    }

    public static KafkaPartitionCommittedOffsetMissingException create() {
        return KafkaPartitionCommittedOffsetMissingException.builder().build();
    }
}
