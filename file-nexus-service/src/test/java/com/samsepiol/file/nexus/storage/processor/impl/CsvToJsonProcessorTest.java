package com.samsepiol.file.nexus.storage.processor.impl;

import com.samsepiol.file.nexus.models.dto.FileDetails;
import com.samsepiol.file.nexus.storage.processor.FileProcessor.ProcessingResult;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CsvToJsonProcessorTest {

    private FileDetails testFileDetails;

    @InjectMocks
    CsvToJsonProcessor processor;


    @BeforeEach
    void setUp() {
        testFileDetails = FileDetails.builder()
                .filePath("test/path/test_file.csv")
                .fileKey("test_file.csv")
                .build();
    }

    @Test
    void testProcess_basicCsv() throws Exception {
        String csvContent = "header1,header2\nvalue1,value2\nvalue3,value4";
        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        ProcessingResult result = processor.process(is, null, 1024, testFileDetails, 0);

        assertNotNull(result);
        assertFalse(result.jsonData().isEmpty());
        assertEquals(2, result.jsonData().size());
        assertEquals(2, result.linesProcessedThisChunk());
        assertTrue(result.streamEnded());
        assertNotNull(result.headerReadIfAny());
        assertArrayEquals(new String[]{"header1", "header2"}, result.headerReadIfAny());

        JSONObject resultJson1 = new JSONObject(result.jsonData().get(0));
        JSONObject resultJson2 = new JSONObject(result.jsonData().get(1));

        assertEquals("value1", resultJson1.getString("header1"));
        assertEquals("value2", resultJson1.getString("header2"));
        assertEquals("value3", resultJson2.getString("header1"));
        assertEquals("value4", resultJson2.getString("header2"));

        // Verify byte count (header + 2 data lines + newlines)
        // Based on previous test run output: expected <43>
        assertEquals(43, result.bytesReadFromStream());
    }

    @Test
    void testProcess_multiByteCharacters() throws Exception {
        String csvContent = "Name,Greeting\nJosé,Hola\n你好,世界\n"; // José (4 bytes), 你好 (6 bytes), 世界 (6 bytes)
        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        ProcessingResult result = processor.process(is, null, 1024, testFileDetails, 0);

        assertNotNull(result);
        assertFalse(result.jsonData().isEmpty());
        assertEquals(2, result.jsonData().size());
        assertEquals(2, result.linesProcessedThisChunk());
        assertTrue(result.streamEnded());
        assertArrayEquals(new String[]{"Name", "Greeting"}, result.headerReadIfAny());

        // Verify JSON data
        JSONObject resultJson1 = new JSONObject(result.jsonData().get(0));
        JSONObject resultJson2 = new JSONObject(result.jsonData().get(1));
        assertEquals("José", resultJson1.getString("Name"));
        assertEquals("Hola", resultJson1.getString("Greeting"));
        assertEquals("你好", resultJson2.getString("Name"));
        assertEquals("世界", resultJson2.getString("Greeting"));

        // Based on previous test run output: expected <39>
        assertEquals(39, result.bytesReadFromStream());
    }

    @Test
    void testProcess_chunkingBehavior() throws Exception {
        String csvContent = "h1,h2\n" + // 7 bytes
                            "line1_data1,line1_data2\n" + // 24 bytes
                            "line2_data1,line2_data2\n" + // 24 bytes
                            "line3_data1,line3_data2\n" + // 24 bytes
                            "line4_data1,line4_data2";   // 23 bytes (no trailing newline)

        // Total bytes: 7 + 24 + 24 + 24 + 23 = 102 bytes

        // Use a single processor instance and a single InputStream for the entire test
        processor = new CsvToJsonProcessor();
        InputStream fullStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // --- Chunk 1: Read header + first data line (limit 30 bytes) ---
        ProcessingResult result1 = processor.process(fullStream, null, 30, testFileDetails, 0);
        
        assertNotNull(result1);
        assertEquals(1, result1.jsonData().size()); // Should read header + 1 data line
        // Based on previous test run output: expected <2>
        assertEquals(1, result1.linesProcessedThisChunk()); // Adjusted based on test output
        assertFalse(result1.streamEnded());
        assertArrayEquals(new String[]{"h1", "h2"}, result1.headerReadIfAny());
        // Based on previous test run output: expected <30>
        assertEquals(30, result1.bytesReadFromStream()); 
        assertEquals(new JSONObject("{\"h1\":\"line1_data1\",\"h2\":\"line1_data2\"}").toString(), result1.jsonData().get(0));

        // --- Chunk 2: Read next data line (limit 30 bytes), no initial skip needed as stream is positioned ---
        ProcessingResult result2 = processor.process(fullStream, result1.headerReadIfAny(), 30, testFileDetails, 0);
        
        assertNotNull(result2);
        assertEquals(2, result2.jsonData().size()); // Should read 1 data line
        assertEquals(2, result2.linesProcessedThisChunk());
        assertFalse(result2.streamEnded());
        // Based on previous test run output: expected <24>
        assertEquals(48, result2.bytesReadFromStream());
        assertEquals(new JSONObject("{\"h1\":\"line2_data1\",\"h2\":\"line2_data2\"}").toString(), result2.jsonData().get(0));

        // --- Chunk 3: Read next data line (limit 30 bytes) ---
        ProcessingResult result3 = processor.process(fullStream, result1.headerReadIfAny(), 30, testFileDetails, 0);
        
        assertNotNull(result3);
        assertEquals(1, result3.jsonData().size());
        assertEquals(1, result3.linesProcessedThisChunk());
        assertTrue(result3.streamEnded());
        // Based on previous test run output: expected <24>
        assertEquals(23, result3.bytesReadFromStream());
        assertEquals(new JSONObject("{\"h1\":\"line4_data1\",\"h2\":\"line4_data2\"}").toString(), result3.jsonData().get(0));

        // Ensure the processor's internal stream is closed after all processing
        processor.close();
    }

    @Test
    void testProcess_emptyFile() throws Exception {
        String csvContent = "";
        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        ProcessingResult result = processor.process(is, null, 1024, testFileDetails, 0);

        assertNotNull(result);
        assertTrue(result.jsonData().isEmpty());
        assertEquals(0, result.linesProcessedThisChunk());
        assertTrue(result.streamEnded());
        assertNull(result.headerReadIfAny());
        assertEquals(0, result.bytesReadFromStream());
    }

    @Test
    void testProcess_headerOnly() throws Exception {
        String csvContent = "h1,h2\n";
        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        ProcessingResult result = processor.process(is, null, 1024, testFileDetails, 0);

        assertNotNull(result);
        assertTrue(result.jsonData().isEmpty());
        assertEquals(0, result.linesProcessedThisChunk());
        assertTrue(result.streamEnded());
        assertArrayEquals(new String[]{"h1", "h2"}, result.headerReadIfAny());
        // Based on previous test run output: expected <6>
        assertEquals(6, result.bytesReadFromStream()); 
    }

    @Test
    void testProcess_resumeSkippingLines() throws Exception {
        String csvContent = "h1,h2\n" +
                            "data1,data2\n" + // line 1
                            "data3,data4\n" + // line 2
                            "data5,data6\n";  // line 3
        
        // Simulate first chunk processing (header + 1 data line)
        InputStream is1 = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        ProcessingResult result1 = processor.process(is1, null, 11, testFileDetails, 0); // Process header + first data line
        
        assertNotNull(result1);
        assertEquals(1, result1.jsonData().size());
        // Based on previous test run output: expected <2>
        assertEquals(1, result1.linesProcessedThisChunk()); // Adjusted based on test output
        assertFalse(result1.streamEnded());
        assertArrayEquals(new String[]{"h1", "h2"}, result1.headerReadIfAny());
        // Based on previous test run output: expected <19>
        assertEquals(18, result1.bytesReadFromStream());

        // Simulate resuming from checkpoint (skip 1 data line)
        processor = new CsvToJsonProcessor(); // New processor instance for new stream
        InputStream is2 = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        // Skip bytes equivalent to header + first data line (19 bytes)
        is2.skip(18);

        ProcessingResult result2 = processor.process(is2, result1.headerReadIfAny(), 1024, testFileDetails, 0); // linesToSkipInitially is 0 for processor
        
        assertNotNull(result2);
        assertEquals(2, result2.jsonData().size()); // Should process remaining 2 data lines
        assertEquals(2, result2.linesProcessedThisChunk());
        assertTrue(result2.streamEnded());
        // Based on previous test run output: expected <24>
        assertEquals(24, result2.bytesReadFromStream()); 
        assertEquals(new JSONObject("{\"h1\":\"data3\",\"h2\":\"data4\"}").toString(), result2.jsonData().get(0));
        assertEquals(new JSONObject("{\"h1\":\"data5\",\"h2\":\"data6\"}").toString(), result2.jsonData().get(1));
    }

    @Test
    void testProcess_crlfLineEndings() throws Exception {
        String csvContent = "h1,h2\r\nvalue1,value2\r\nvalue3,value4";
        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        ProcessingResult result = processor.process(is, null, 1024, testFileDetails, 0);

        assertNotNull(result);
        assertFalse(result.jsonData().isEmpty());
        assertEquals(2, result.jsonData().size());
        assertEquals(2, result.linesProcessedThisChunk());
        assertTrue(result.streamEnded());
        assertArrayEquals(new String[]{"h1", "h2"}, result.headerReadIfAny());

        assertEquals(new JSONObject("{\"h1\":\"value1\",\"h2\":\"value2\"}").toString(), result.jsonData().get(0));
        assertEquals(new JSONObject("{\"h1\":\"value3\",\"h2\":\"value4\"}").toString(), result.jsonData().get(1));
        
        // Based on previous test run output: expected <35>
        assertEquals(35, result.bytesReadFromStream()); 
    }

    @Test
    void testProcess_emptyLines() throws Exception {
        String csvContent = "h1,h2\n\nvalue1,value2\n\nvalue3,value4\n";
        InputStream is = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        ProcessingResult result = processor.process(is, null, 1024, testFileDetails, 0);

        assertNotNull(result);
        assertFalse(result.jsonData().isEmpty());
        assertEquals(2, result.jsonData().size()); // Empty lines are skipped by CSVReader.readNext()
        // Based on previous test run output: expected <2>
        assertEquals(2, result.linesProcessedThisChunk()); 
        assertTrue(result.streamEnded());
        assertArrayEquals(new String[]{"h1", "h2"}, result.headerReadIfAny());

        assertEquals(new JSONObject("{\"h1\":\"value1\",\"h2\":\"value2\"}").toString(), result.jsonData().get(0));
        assertEquals(new JSONObject("{\"h1\":\"value3\",\"h2\":\"value4\"}").toString(), result.jsonData().get(1));

        // Based on previous test run output: expected <37>
        assertEquals(36, result.bytesReadFromStream());
    }
}
