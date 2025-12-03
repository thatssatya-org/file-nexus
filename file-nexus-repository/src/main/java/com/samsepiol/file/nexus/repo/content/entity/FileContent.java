package com.samsepiol.file.nexus.repo.content.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samsepiol.file.nexus.repo.constants.RepositoryConstants;
import com.samsepiol.library.mongo.models.Entity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileContent extends Entity {
    private static final String ID_PREFIX = "FC";

    @NonNull
    @JsonProperty(RepositoryConstants.FileContentEntityConstants.FILE_ID)
    String fileId;

    @NonNull
    String rowNumber;

    @JsonProperty(RepositoryConstants.FileContentEntityConstants.INDEX_1)
    String index1;

    @JsonProperty(RepositoryConstants.FileContentEntityConstants.INDEX_2)
    String index2;

    @JsonProperty(RepositoryConstants.FileContentEntityConstants.INDEX_3)
    String index3;

    String content;

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
