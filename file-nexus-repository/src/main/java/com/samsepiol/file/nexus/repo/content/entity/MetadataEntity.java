package com.samsepiol.file.nexus.repo.content.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.library.mongo.models.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Jacksonized
@SuperBuilder
public class MetadataEntity extends Entity {
    private static final String ID_PREFIX = "MD";

    @NonNull
    private String fileType;

    @NonNull
    private String date;

    @NonNull
    private MetadataStatus status;

    @NonNull
    private String fileName;

    @Override
    protected @NonNull String getIdPrefix() {
        return ID_PREFIX;
    }
}
