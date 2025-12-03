package com.samsepiol.file.nexus.metadata.workflow.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateMetaDataStatusWorkflowActivityPrefixes {
    public static final String FETCH_END_OFFSETS = "FetchEndOffsetListActivity";
    public static final String MATCH_END_OFFSETS = "MatchOffsetsActivity";
    public static final String UPDATE_FILE_METADATA_STATUS = "UpdateStatusActivity";
}