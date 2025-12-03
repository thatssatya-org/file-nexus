package com.samsepiol.file.nexus.ingestion.workflow.activities.impl;

import com.samsepiol.file.nexus.ingestion.workflow.activities.IKafkaFileProcessingActivity;
import com.samsepiol.file.nexus.models.request.KafkaFileProcessingWorkflowRequest;
import com.samsepiol.file.nexus.storage.hook.StorageHook;
import com.samsepiol.file.nexus.storage.processor.FileProcessor;
import com.samsepiol.file.nexus.storage.processor.FileProcessor.ProcessingResult;
import com.samsepiol.file.nexus.storage.processor.impl.CsvToJsonProcessor;
import com.samsepiol.file.nexus.storage.service.StorageHookMonitoringService;

import com.samsepiol.kafka.client.IProducerClient;
import com.samsepiol.temporal.annotations.TemporalActivity;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@TemporalActivity
public class KafkaFileProcessingActivityImpl implements IKafkaFileProcessingActivity {

    private final Map<String, FileProcessor> fileProcessors = new ConcurrentHashMap<>();
    private final StorageHookMonitoringService storageHookMonitoringService;
    private final IProducerClient producerClient;
    
    

    // 8 KB, this is just a hint for the processor to optimize its read size,
    // processor may or may not read the exact number of bytes
    private final static int CHUNK_PROCESSING_BYTES_HINT = 8192;

    private static final String KAFKA_RECORDS_SENT_COUNT_METRIC = "file.nexus.kafka.records.sent.count";
    private static final String KAFKA_RECORDS_FAILED_COUNT_METRIC = "file.nexus.kafka.records.failed.count";

    private static final String KAFKA_PROCESSING_STARTED_EVENT = "FILE_NEXUS_KAFKA_PROCESSING_STARTED";
    private static final String KAFKA_PROCESSING_COMPLETED_EVENT = "FILE_NEXUS_KAFKA_PROCESSING_COMPLETED";

    @Override
    public void processAndSendToKafka(KafkaFileProcessingWorkflowRequest request) {
        final String sourceName = request.getSourceName();
        final String fileName = request.getFileName();
        final String filePath = request.getFileDetails().getFilePath();
        final String topicName = request.getKafkaDestinationConfig().getTopicName();
        final String key = request.getKafkaDestinationConfig().getKey();
        final String processorType = request.getKafkaDestinationConfig().getProcessorType();
        final long fileSize = request.getFileDetails().getSize();
        
        ActivityExecutionContext context = Activity.getExecutionContext();
        String fileKey = request.getFileDetails().getFileKey();
        Optional<Heartbeat> checkpoint = context.getHeartbeatDetails(Heartbeat.class);

        String errorMessage = null;
        long totalDataRecordsSent = 0;
        long totalRecordsFailed = 0;
        long totalBytesProcessed = 0;

        try {
            StorageHook storageHook = storageHookMonitoringService.getStorageHook(sourceName)
                    .orElseThrow(() -> new RuntimeException("No storage hook found for source: " + sourceName));

            FileProcessor processor = getFileProcessor(processorType);

            long bytesProcessedSoFarAccumulator = checkpoint.map(Heartbeat::bytesProcessed).orElse(0L);
            long linesProcessedSoFarAccumulator = checkpoint.map(Heartbeat::linesProcessed).orElse(0L);
            totalDataRecordsSent = linesProcessedSoFarAccumulator;
            String[] header = checkpoint.map(Heartbeat::header).orElse(null);
            boolean streamEnded = false;

            if (checkpoint.isPresent()) {
                log.info("Resuming processing for file {} from byte offset {} (data line {}).", fileName, bytesProcessedSoFarAccumulator, linesProcessedSoFarAccumulator);
            } else {
                emitProcessingStartedEvent(sourceName, fileName, filePath, fileSize, topicName, processorType,
                        System.currentTimeMillis());
                log.info("Starting new processing for file {}", fileName);
            }

            try (InputStream inputStream = storageHook.getFileAsStream(fileKey, bytesProcessedSoFarAccumulator)) {
                if (inputStream == null) {
                    log.warn("File {} stream is null from storageHook.getFileAsStream, skipping Kafka send.", fileName);
                    errorMessage = "File stream is null from storageHook.getFileAsStream";
                    return;
                }
                log.info("Successfully obtained input stream for file {}", fileName);

                while (!streamEnded) {
                    long linesToSkipInThisProcessorCall = checkpoint.isPresent() ? linesProcessedSoFarAccumulator : 0;

                    log.info("Processing file {}. Processor will be asked to skip {} lines from its current stream position (only if first chunk and resuming). Total bytes processed so far: {}",
                            fileName, linesToSkipInThisProcessorCall, bytesProcessedSoFarAccumulator);

                    ProcessingResult result = processor.process(
                            inputStream,
                            header,
                            CHUNK_PROCESSING_BYTES_HINT,
                            request.getFileDetails(),
                            linesToSkipInThisProcessorCall
                    );

                    if (result.headerReadIfAny() != null) {
                        header = result.headerReadIfAny();
                        log.info("Header read/updated for file {}: {}", fileName, String.join(", ", header));
                    }

                    for (String jsonRecord : result.jsonData()) {
                        try {
                            log.info("Sending JSON record to Kafka topic {}: {}", topicName, jsonRecord);
                            producerClient.sendMessage("surrogate", topicName,
                                    getKafkaKey(jsonRecord,key, fileName),
                                    jsonRecord);
                            totalDataRecordsSent++;
                        } catch (Exception kafkaException) {
                            log.error("Failed to send record to Kafka for file {}: {}", fileName, kafkaException.getMessage(), kafkaException);
                            totalRecordsFailed++;
                        }
                    }

                    log.info("file {}: {} data records sent ({} bytes read by processor in this chunk). Actual data lines processed by processor in this chunk: {}.",
                            fileName, result.jsonData().size(), result.bytesReadFromStream(), result.linesProcessedThisChunk());

                    bytesProcessedSoFarAccumulator += result.bytesReadFromStream();
                    linesProcessedSoFarAccumulator += result.linesProcessedThisChunk();
                    totalBytesProcessed = bytesProcessedSoFarAccumulator;
                    streamEnded = result.streamEnded();

                    if (result.jsonData().isEmpty() && !streamEnded) {
                        log.warn("No data processed for file {} but stream not ended. Breaking to prevent potential infinite loop.", fileName);
                        break;
                    }

                    if (!streamEnded) {
                        Activity.getExecutionContext().heartbeat(new Heartbeat(
                                bytesProcessedSoFarAccumulator,
                                linesProcessedSoFarAccumulator,
                                header
                        ));
                        log.debug("Heartbeat sent for file {} after processing up to byte offset {} (data line {}).",
                                fileName, bytesProcessedSoFarAccumulator, linesProcessedSoFarAccumulator);
                    }
                }
                log.info("Finished streaming process for file {}. Total data records sent: {}. Total bytes checkpoint: {}. Total data lines checkpoint: {}",
                        fileName, totalDataRecordsSent, bytesProcessedSoFarAccumulator, linesProcessedSoFarAccumulator);

            } finally {
                try {
                    if (processor != null) {
                        processor.close();
                    }
                } catch (IOException e) {
                    log.error("Error closing file processor for file {}: {}", fileName, e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            log.error("IOException during file streaming or processing for file {}: {}", fileName, e.getMessage(), e);
            errorMessage = e.getMessage();
            throw new RuntimeException("Failed to process file due to IOException: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing and sending file {} to Kafka: {}", fileName, e.getMessage(), e);
            errorMessage = e.getMessage();
            throw new RuntimeException("Failed to process and send file to Kafka: " + e.getMessage(), e);
        } finally {
            // Emit enhanced metrics and completion event
            emitKafkaProcessingMetrics(sourceName, topicName, processorType,
                    totalDataRecordsSent, totalRecordsFailed);
            emitProcessingCompletedEvent(sourceName, fileName, filePath, topicName, processorType,
                    totalDataRecordsSent, totalBytesProcessed,
                                       errorMessage, System.currentTimeMillis());
        }
    }

    private String getKafkaKey(String jsonRecord, String key, String fileName) {
        if(ObjectUtils.isEmpty(key)) {
            log.warn("Kafka key is empty for file '{}'. Using file name as key.", fileName);
            return null; // Fallback to null if key is empty, which means Kafka will use the default partitioning strategy
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonRecord);
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            } else {
                log.warn("Key '{}' not found in JSON record for file '{}'. Using file name as key.", key, fileName);
                return fileName; // Fallback to file name if key not found
            }
        } catch (Exception e) {
            log.error("Error extracting key '{}' from JSON record for file '{}': {}", key, fileName, e.getMessage(), e);
            return fileName; // Fallback to the file name in case of error
        }
    }

    private FileProcessor getFileProcessor(String processorType) {
        return fileProcessors.computeIfAbsent(processorType, key -> {
            if ("CSV_TO_JSON".equals(key)) {
                return new CsvToJsonProcessor(eventHelper, metricHelper);
            }
            throw new IllegalArgumentException("Unknown processor type: " + processorType);
        });
    }

    /**
     * Emit enhanced metrics for Kafka processing operations
     */
    private void emitKafkaProcessingMetrics(String sourceName, String topicName, String processorType,
                                            long recordsSent,
                                            long recordsFailed) {
        try {
            Map<String, String> tags = Map.of(
                "sourceName", sourceName,
                "topicName", topicName,
                "processorType", processorType
            );

            if (recordsSent > 0) {
                metricHelper.incrementCounter(KAFKA_RECORDS_SENT_COUNT_METRIC, tags, recordsSent);
            }

            if (recordsFailed > 0) {
                metricHelper.incrementCounter(KAFKA_RECORDS_FAILED_COUNT_METRIC, tags, recordsFailed);
            }
            
        } catch (Exception e) {
            log.error("Failed to emit Kafka processing metrics for source: {}, topic: {}", 
                     sourceName, topicName, e);
        }
    }

    /**
     * Emit Kafka processing started event
     */
    private void emitProcessingStartedEvent(String sourceName, String fileName, String filePath, 
                                          long fileSize, String topicName, String processorType, 
                                          long timestamp) {
        try {
            KafkaProcessingStartedEvent event = new KafkaProcessingStartedEvent(
                sourceName, fileName, filePath, fileSize, topicName, processorType, timestamp
            );
            // TODOeventHelper.publishSync(KAFKA_PROCESSING_STARTED_EVENT, event);
        } catch (Exception e) {
            log.error("Failed to emit Kafka processing started event for file: {}", fileName, e);
        }
    }

    /**
     * Emit Kafka processing completed event
     */
    private void emitProcessingCompletedEvent(String sourceName, String fileName, String filePath, 
                                            String topicName, String processorType,
                                              long recordsSent, long bytesProcessed,
                                            String errorMessage, long timestamp) {
        try {
            KafkaProcessingCompletedEvent event = new KafkaProcessingCompletedEvent(
                sourceName, fileName, filePath, topicName, processorType,
                    recordsSent, bytesProcessed, errorMessage, timestamp
            );
            // TODOeventHelper.publishSync(KAFKA_PROCESSING_COMPLETED_EVENT, event);
        } catch (Exception e) {
            log.error("Failed to emit Kafka processing completed event for file: {}", fileName, e);
        }
    }


    private record Heartbeat(long bytesProcessed, long linesProcessed, String[] header) {}
    
    private record KafkaProcessingStartedEvent(String sourceName, String fileName, String filePath, 
                                             long fileSize, String topicName, String processorType, 
                                             long timestamp) {}
    
    private record KafkaProcessingCompletedEvent(String sourceName, String fileName, String filePath, 
                                               String topicName, String processorType,
                                                 long recordsSent, long bytesProcessed,
                                               String errorMessage, long timestamp) {}

}
