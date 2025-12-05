package com.samsepiol.file.nexus.storage.destination;

/**
 * Enum for transformer types.
 * Defines the supported file format transformations.
 */
public enum TransformerType {
    /**
     * Transforms CSV files to XLSX format.
     */
    CSV_TO_XLSX,
    
    /**
     * No transformation is applied.
     */
    NONE
}