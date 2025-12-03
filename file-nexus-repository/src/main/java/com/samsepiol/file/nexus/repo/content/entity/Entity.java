package com.samsepiol.file.nexus.repo.content.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "parentBuilder")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public abstract class Entity {
    @NonNull
    @JsonProperty("_id")
    private final String id;

    @NonNull
    private final Long createdAt;

    @NonNull
    @Setter
    private Long updatedAt;
}
