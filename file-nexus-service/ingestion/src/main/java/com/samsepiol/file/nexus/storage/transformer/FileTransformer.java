package com.samsepiol.file.nexus.storage.transformer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.samsepiol.file.nexus.storage.destination.TransformerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for transforming files from one format to another.
 */
@Slf4j
public class FileTransformer {

    /**
     * Transforms a file from one format to another based on the specified transformer type.
     * This version works with InputStreams directly to avoid unnecessary byte array conversions.
     *
     * @param inputStream    The input stream of the file to transform
     * @param fileName       The name of the file
     * @param transformerType The type of transformation to apply
     * @return A TransformationResult containing the transformed content and new file name,
     *         or the original content and file name if no transformation was applied
     * @throws IOException If an I/O error occurs
     */
    public static TransformationResult transformFile(InputStream inputStream, String fileName, TransformerType transformerType) throws IOException {
        if (transformerType == null || transformerType == TransformerType.NONE) {
            return getTransformationResult(inputStream.readAllBytes(), fileName);
        }

        try {
            if (transformerType == TransformerType.CSV_TO_XLSX) {
                if (fileName.toLowerCase().endsWith(".csv")) {
                    log.info("Transforming CSV file {} to XLSX format using stream", fileName);
                    String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".xlsx";
                    return getTransformationResult(convertCsvToXlsx(inputStream), newFileName);
                } else {
                    log.info("File {} is not a CSV file, skipping transformation", fileName);
                    return getTransformationResult(inputStream.readAllBytes(), fileName);
                }
            } else {
                log.warn("Unsupported transformer type: {}", transformerType);
                return getTransformationResult(inputStream.readAllBytes(), fileName);
            }
        } catch (Exception e) {
            log.error("Error transforming file {}: {}", fileName, e.getMessage(), e);
            // In case of error, we still need to read the stream to avoid resource leaks
            return getTransformationResult(inputStream.readAllBytes(), fileName);
        }
    }

    private static TransformationResult getTransformationResult(byte[] inputStream, String fileName) throws IOException {
        byte[] content = inputStream;
        return new TransformationResult(content, fileName);
    }

    /**
     * Converts CSV content to XLSX format.
     *
     * @param inputStream The input stream containing CSV content
     * @return The XLSX content as a byte array
     * @throws IOException If an I/O error occurs
     */
    private static byte[] convertCsvToXlsx(InputStream inputStream) throws IOException {
        try (
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVReader csvReader = new CSVReader(reader);
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Sheet1");

            String[] nextLine;
            int rowNum = 0;

            try {
                // Process each line of the CSV file
                while ((nextLine = csvReader.readNext()) != null) {
                    Row currentRow = sheet.createRow(rowNum++);

                    for (int i = 0; i < nextLine.length; i++) {
                        Cell cell = currentRow.createCell(i);
                        cell.setCellValue(nextLine[i]);
                    }
                }

                // Write the workbook to the output stream
                workbook.write(outputStream);
                return outputStream.toByteArray();

            } catch (CsvValidationException e) {
                log.error("Error validating CSV content: {}", e.getMessage(), e);
                throw new IOException("Error validating CSV content", e);
            }
        }
    }

    /**
     * Result of a file transformation.
     */
    public record TransformationResult(byte[] content, String fileName) {
    }
}
