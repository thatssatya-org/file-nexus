package com.samsepiol.file.nexus.storage.config.destination;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.samsepiol.file.nexus.storage.destination.DestinationType;
import com.samsepiol.file.nexus.storage.destination.TransformerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Configuration for SMTP destination.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SmtpConfig extends AbstractDestinationConfig {

    private String to;
    private String cc;
    private String from;
    private String subject;
    private String body;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean ssl;
    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    private TransformerType transformerType = TransformerType.NONE;

    @Override
    public DestinationType getType() {
        return DestinationType.SMTP;
    }
}
