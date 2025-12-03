package com.samsepiol.file.nexus.repo.content.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.file.nexus.enums.FileStateStatus;
import com.samsepiol.library.mongo.models.Entity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Jacksonized
@SuperBuilder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class FileStateEntity extends Entity {
    private static final String ID_PREFIX = "FS";

    @NonNull
    private String filePath;

    @NonNull
    private String hookName;

    @NonNull
    private FileStateStatus status;

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
