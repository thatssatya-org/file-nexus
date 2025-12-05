package com.samsepiol.file.nexus.storage.processor;

import com.samsepiol.file.nexus.models.dto.FileDetails;
import com.samsepiol.file.nexus.models.enums.ProcessorType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileProcessor {

    ProcessingResult process(
            InputStream inputStream,
            String[] cachedHeader,        // Header from previous chunk, or null if first chunk/first call
            int maxBytesToProcess,        // Hint for how much to process (bytes)
            FileDetails fileDetails,      // For logging or context
            long linesToSkipInitially     // Number of lines to skip from the beginning of the provided stream
    ) throws com.opencsv.exceptions.CsvValidationException, java.io.IOException;

    ProcessorType getType();

    void close() throws IOException;

    record ProcessingResult(
            List<String> jsonData,      // List of JSON strings for Kafka
            long bytesReadFromStream,   // How many bytes were consumed from the input stream
            String[] headerReadIfAny,   // The header if it was read in this call, null otherwise
            boolean streamEnded,        // True if the end of the input stream was reached
            long linesProcessedThisChunk // Number of data lines processed in this chunk
    ) {
    }
}
