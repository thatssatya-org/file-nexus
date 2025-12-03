package com.samsepiol.file.nexus.storage.processor.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvMalformedLineException;
import com.opencsv.exceptions.CsvValidationException;
import com.samsepiol.file.nexus.models.dto.FileDetails;
import com.samsepiol.file.nexus.models.enums.ProcessorType;
import com.samsepiol.file.nexus.storage.processor.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CsvToJsonProcessor implements FileProcessor {

    private static final int BUFFER_SIZE = 8192;
    private static final String ROW_EVENT_NAME = "FILE_NEXUS_CSV_ROW_PROCESSOR";

    private static final String CSV_ROWS_PROCESSED_COUNT_METRIC = "file.nexus.processor.csv.rows.processed.count";
    private static final String CSV_ROWS_FAILED_COUNT_METRIC = "file.nexus.processor.csv.rows.failed.count";

    
    
    private BufferedInputStream currentInputStream;


    @Override
    public ProcessingResult process(
            InputStream inputStream,
            String[] cachedHeader,
            int maxBytesToProcess,
            FileDetails fileDetails,
            long linesToSkipInitially) throws CsvValidationException, IOException {
        
        ProcessingContext context = initializeProcessing(inputStream, cachedHeader);
        context.rowsProcessedCount = 0;
        context.rowsFailedCount = 0;

        try {
            HeaderProcessingResult headerResult = processHeader(context, linesToSkipInitially, fileDetails);
            if (headerResult.shouldReturn()) {
                // Emit metrics even for early returns
                emitCsvProcessingMetrics(fileDetails, context.rowsProcessedCount, context.rowsFailedCount);
                return headerResult.result;
            }

            context.headerForCurrentProcessing = headerResult.headerForCurrentProcessing;
            context.headerReadInThisChunk = headerResult.headerReadInThisChunk;
            context.bytesReadThisCall = headerResult.bytesReadThisCall;

            DataProcessingResult dataResult = processDataLines(context, maxBytesToProcess, fileDetails);

            ProcessingResult finalResult = createFinalResult(context, dataResult);
            
            // Emit metrics for successful processing
            emitCsvProcessingMetrics(fileDetails, context.rowsProcessedCount, context.rowsFailedCount);
            
            return finalResult;

        } catch (IOException e) {
            log.error("IOException during CSV processing for file {}: {}", fileDetails.getFilePath(), e.getMessage(), e);
            // Emit metrics for failed processing
            emitCsvProcessingMetrics(fileDetails, context.rowsProcessedCount, context.rowsFailedCount);
            throw e;
        } catch (CsvValidationException e) {
            log.error("CSV validation error during processing for file {}: {}", fileDetails.getFilePath(), e.getMessage(), e);
            // Emit metrics for failed processing
            emitCsvProcessingMetrics(fileDetails, context.rowsProcessedCount, context.rowsFailedCount);
            throw e;
        }
    }

    private ProcessingContext initializeProcessing(InputStream inputStream, String[] cachedHeader) {
        ProcessingContext context = new ProcessingContext();
        context.jsonDataList = new ArrayList<>();
        context.bytesReadThisCall = 0;
        context.headerForCurrentProcessing = cachedHeader;
        context.headerReadInThisChunk = null;
        context.streamEnded = false;
        context.dataLinesProcessedThisChunk = 0;
        context.line = null;
        context.limitReached = false;

        if (currentInputStream == null) {
            currentInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
        }
        context.currentInputStream = currentInputStream;

        return context;
    }

    private HeaderProcessingResult processHeader(ProcessingContext context, long linesToSkipInitially, FileDetails fileDetails)
            throws IOException, CsvValidationException {

        if (linesToSkipInitially == 0 && context.headerForCurrentProcessing == null) {
            return processHeaderFromBeginning(context, fileDetails);
        } else if (linesToSkipInitially > 0 && context.headerForCurrentProcessing == null) {
            return handleMissingCachedHeader(context, linesToSkipInitially, fileDetails);
        } else {
            log.debug("Using cached header for file {}. Lines to skip initially: {}", fileDetails.getFilePath(), linesToSkipInitially);
        }

        if (context.headerForCurrentProcessing == null) {
            log.error("Critical error: No header available for processing CSV file {}. Aborting chunk.", fileDetails.getFilePath());
            context.streamEnded = true;
            ProcessingResult result = new ProcessingResult(context.jsonDataList, context.bytesReadThisCall,
                    context.headerReadInThisChunk, context.streamEnded, 0);
            return new HeaderProcessingResult(result, true, null, null, 0);
        }

        return new HeaderProcessingResult(null, false, context.headerForCurrentProcessing,
                context.headerReadInThisChunk, context.bytesReadThisCall);
    }

    private HeaderProcessingResult processHeaderFromBeginning(ProcessingContext context, FileDetails fileDetails)
            throws IOException, CsvValidationException {

        log.info("Processing file {} from the beginning. Attempting to read header.", fileDetails.getFilePath());
        LineReadResult headerResult = readLineWithByteCount(context.currentInputStream);

        if (headerResult != null) {
            context.line = headerResult.line();
            context.bytesReadThisCall += headerResult.byteLength();

            try (CSVReader csvHeaderReader = new CSVReader(new java.io.StringReader(context.line))) {
                context.headerForCurrentProcessing = csvHeaderReader.readNext();
            }

            if (context.headerForCurrentProcessing != null) {
                context.headerReadInThisChunk = context.headerForCurrentProcessing;
                log.info("Header read for file {}: {}", fileDetails.getFilePath(), String.join(", ", context.headerForCurrentProcessing));
            } else {
                log.warn("CSV file {} appears to be empty or header is missing at initial read.", fileDetails.getFilePath());
                context.streamEnded = true;
                ProcessingResult result = new ProcessingResult(context.jsonDataList, context.bytesReadThisCall,
                        context.headerReadInThisChunk, context.streamEnded, 0);
                return new HeaderProcessingResult(result, true, null, null, 0);
            }
        } else {
            log.warn("CSV file {} appears to be empty or header is missing at initial read. Ending stream.", fileDetails.getFilePath());
            context.streamEnded = true;
            ProcessingResult result = new ProcessingResult(context.jsonDataList, context.bytesReadThisCall,
                    context.headerReadInThisChunk, context.streamEnded, 0);
            return new HeaderProcessingResult(result, true, null, null, 0);
        }

        return new HeaderProcessingResult(null, false, context.headerForCurrentProcessing,
                context.headerReadInThisChunk, context.bytesReadThisCall);
    }

    private HeaderProcessingResult handleMissingCachedHeader(ProcessingContext context, long linesToSkipInitially, FileDetails fileDetails) {
        log.error("Error: Resuming CSV processing for file {} (skipping {} lines) but no cached header provided. Cannot proceed.",
                fileDetails.getFilePath(), linesToSkipInitially);
        context.streamEnded = true;
        ProcessingResult result = new ProcessingResult(context.jsonDataList, context.bytesReadThisCall,
                null, context.streamEnded, 0);
        return new HeaderProcessingResult(result, true, null, null, 0);
    }

    private DataProcessingResult processDataLines(ProcessingContext context, int maxBytesToProcess, FileDetails fileDetails)
            throws IOException, CsvValidationException {

        while (context.bytesReadThisCall < maxBytesToProcess) {
            LineReadResult lineResult = readLineWithByteCount(context.currentInputStream);
            if (lineResult == null) {
                context.streamEnded = true;
                break;
            }

            context.line = lineResult.line();
            long lineByteLength = lineResult.byteLength();

            if (context.bytesReadThisCall + lineByteLength > maxBytesToProcess && context.dataLinesProcessedThisChunk > 0) {
                context.limitReached = true;
            }

            context.bytesReadThisCall += lineByteLength;

            processDataLine(context, fileDetails);

            if (context.limitReached) {
                log.info("Max bytes limit reached for file {}. Stopping further processing.", fileDetails.getFilePath());
                break;
            }
        }

        if (context.line == null && !context.streamEnded) {
            log.info("End of CSV stream reached for file {}.", fileDetails.getFilePath());
            context.streamEnded = true;
        }

        return new DataProcessingResult(context.streamEnded);
    }

    private void processDataLine(ProcessingContext context, FileDetails fileDetails) throws IOException, CsvValidationException {
        try (CSVReader csvLineReader = new CSVReader(new java.io.StringReader(cleanupLine(context.line)))) {
            String[] record = csvLineReader.readNext();

            if (!isValidRecord(record, context.headerForCurrentProcessing, fileDetails)) {
                context.rowsFailedCount++;
                emitRowProcessedEvent(record, context.headerForCurrentProcessing, fileDetails.getFilePath(), false);
                return;
            }

            if (record != null) {
                String jsonRecord = createJsonFromRecord(record, context.headerForCurrentProcessing);
                context.jsonDataList.add(jsonRecord);
                context.dataLinesProcessedThisChunk++;
                context.rowsProcessedCount++;
            }

            emitRowProcessedEvent(record, context.headerForCurrentProcessing, fileDetails.getFilePath(), true);
        } catch (CsvValidationException e) {
            log.error("CSV validation error for file {}: {}", fileDetails.getFilePath(), e.getMessage(), e);
            context.rowsFailedCount++;
            emitRowProcessedEvent(new String[]{context.line}, context.headerForCurrentProcessing, fileDetails.getFilePath(), false);
        } catch (CsvMalformedLineException e) {
            log.error("Malformed line in CSV file {}: {}", fileDetails.getFilePath(), e.getMessage(), e);
            context.rowsFailedCount++;
            emitRowProcessedEvent(new String[]{context.line}, context.headerForCurrentProcessing, fileDetails.getFilePath(), false);
        } catch (IOException e) {
            log.error("IOException while processing line in CSV file {}: {}", fileDetails.getFilePath(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing line in CSV file {}: {}", fileDetails.getFilePath(), e.getMessage(), e);
            context.rowsFailedCount++;
            emitRowProcessedEvent(new String[]{context.line}, context.headerForCurrentProcessing, fileDetails.getFilePath(), false);
        }
    }

    private String cleanupLine(String line) {
        // remove and " characters from the line
        if (line != null) {
            return line.replaceAll("\"", "").trim();
        }
        return "";
    }

    private boolean isValidRecord(String[] record, String[] header, FileDetails fileDetails) {
        if (record != null && record.length != header.length) {
            log.warn("Record length {} does not match header length {} for file {}. Skipping this record.",
                    record.length, header.length, fileDetails.getFilePath());
            return false;
        }
        return true;
    }

    private String createJsonFromRecord(String[] record, String[] header) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < header.length; i++) {
            jsonObject.put(header[i], record[i]);
        }
        return jsonObject.toString();
    }

    private ProcessingResult createFinalResult(ProcessingContext context, DataProcessingResult dataResult) {
        return new ProcessingResult(context.jsonDataList, context.bytesReadThisCall,
                context.headerReadInThisChunk, dataResult.streamEnded, context.dataLinesProcessedThisChunk);
    }


    private LineReadResult readLineWithByteCount(BufferedInputStream inputStream) throws IOException {
        ByteArrayOutputStream lineContentBytes = new ByteArrayOutputStream();
        int byteRead;
        long bytesConsumedForLine = 0;

        while ((byteRead = inputStream.read()) != -1) {
            bytesConsumedForLine++;
            if (byteRead == '\n') {
                return new LineReadResult(lineContentBytes.toString(StandardCharsets.UTF_8), bytesConsumedForLine);
            }
            if (byteRead == '\r') {
                continue;
            }
            lineContentBytes.write(byteRead);
        }

        // If EOF is reached and there's content in the buffer, it's the last line (without a trailing newline)
        if (lineContentBytes.size() > 0) {
            return new LineReadResult(lineContentBytes.toString(StandardCharsets.UTF_8), bytesConsumedForLine);
        }
        return null;
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.CSV_TO_JSON;
    }

    @Override
    public void close() throws IOException {
        if (currentInputStream != null) {
            currentInputStream.close();
            currentInputStream = null;
        }
    }

    private void emitRowProcessedEvent(String[] values, String[] headers, String filePath, boolean processed) {
        Row row = new Row(values, headers, processed, filePath);
        // TODOeventHelper.publishSync(ROW_EVENT_NAME, row);
    }

    /**
     * Emit CSV processing metrics
     */
    private void emitCsvProcessingMetrics(FileDetails fileDetails, long rowsProcessed, long rowsFailed) {
        try {
            Map<String, String> tags = Map.of(
                "processorType", "CSV_TO_JSON"
            );
            
            // Emit rows processed count metric
            if (rowsProcessed > 0) {
                metricHelper.incrementCounter(CSV_ROWS_PROCESSED_COUNT_METRIC, tags, rowsProcessed);
            }
            
            // Emit rows failed count metric
            if (rowsFailed > 0) {
                metricHelper.incrementCounter(CSV_ROWS_FAILED_COUNT_METRIC, tags, rowsFailed);
            }
            
        } catch (Exception e) {
            log.error("Failed to emit CSV processing metrics for file: {}", fileDetails.getFilePath(), e);
        }
    }

    private static class ProcessingContext {
        List<String> jsonDataList;
        long bytesReadThisCall;
        String[] headerForCurrentProcessing;
        String[] headerReadInThisChunk;
        boolean streamEnded;
        long dataLinesProcessedThisChunk;
        String line;
        boolean limitReached;
        BufferedInputStream currentInputStream;
        long rowsProcessedCount;
        long rowsFailedCount;
    }

    private record HeaderProcessingResult(
            ProcessingResult result,
            boolean shouldReturn,
            String[] headerForCurrentProcessing,
            String[] headerReadInThisChunk,
            long bytesReadThisCall
    ) {
    }

    private record DataProcessingResult(boolean streamEnded) {
    }

    private record LineReadResult(String line, long byteLength) {
    }

    private record Row(String[] values, String[] headers, boolean processed, String filePath) {
    }
}
