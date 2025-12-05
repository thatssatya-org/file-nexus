package com.samsepiol.file.nexus.storage.config.destination;

import com.samsepiol.file.nexus.storage.destination.DestinationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for destination configurations.
 * Provides common fields and methods for all destination types.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDestinationConfig {

    /**
     * The name of the destination.
     */
    private String name;

    /**
     * Whether this destination is enabled.
     * -- GETTER --
     * Check if the destination is enabled.
     */
    private boolean enabled = false;

    /**
     * Get the destination type.
     * This method must be implemented by subclasses.
     *
     * @return The destination type
     */
    public abstract DestinationType getType();

}
