package com.samsepiol.file.nexus.content.data.models.enums;

import com.samsepiol.file.nexus.repo.constants.RepositoryConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Index {
    FIRST(RepositoryConstants.FileContentEntityConstants.INDEX_1),
    SECOND(RepositoryConstants.FileContentEntityConstants.INDEX_2),
    THIRD(RepositoryConstants.FileContentEntityConstants.INDEX_3);

    private final String value;
}
