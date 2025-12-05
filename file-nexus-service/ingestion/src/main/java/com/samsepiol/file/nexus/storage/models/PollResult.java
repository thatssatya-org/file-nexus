package com.samsepiol.file.nexus.storage.models;

import java.time.Instant;
import java.util.List;

public record PollResult(List<FileInfo> files, Instant latestTimestamp) {
}
