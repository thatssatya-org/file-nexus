package com.samsepiol.file.nexus.content.data.parser.impl;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.data.models.response.TudfFileContent;
import com.samsepiol.file.nexus.content.data.parser.FileContentParser;
import com.samsepiol.file.nexus.content.data.parser.config.TudfFileParserConfig;
import com.samsepiol.file.nexus.content.data.parser.exception.TudfFileContentParsingException;
import com.samsepiol.file.nexus.content.data.parser.models.enums.FileParserType;
import com.samsepiol.file.nexus.content.data.parser.models.request.FileContentParsingRequest;
import com.samsepiol.file.nexus.content.exception.FileContentParsingException;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import com.samsepiol.file.nexus.utils.ClassUtils;
import com.samsepiol.helper.utils.CommonSerializationUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TudfContentParser implements FileContentParser {
    private final FileSchemaConfig fileConfig;

    @Override
    public @NonNull Map<String, Object> parse(@NonNull FileContentParsingRequest request)
            throws FileContentParsingException, UnsupportedFileException {

        var fileParserConfig = getTudfFileParserConfig(request);
        String content = request.getContent();
        TudfFileContent tudfFileContent = CommonSerializationUtil.readObject(content, TudfFileContent.class);

        String message = tudfFileContent.getPayload().getMessage();
        if (StringUtils.isBlank(message)) {
            log.info("empty message string for rowNumber {} so ignoring..... ", tudfFileContent.getPayload().getRowNumber());
            return Map.of();
        }
        var recordTypeSymbol = getRecordTypeSymbol(message, fileParserConfig);

        if (ObjectUtils.isEmpty(fileParserConfig.getIdentifiers()) || !fileParserConfig.getIdentifiers().containsKey(recordTypeSymbol)) {
            return Map.of();
        }
        var fieldsList = fileParserConfig.getIdentifiers().get(recordTypeSymbol).getFieldMapping();
        var map = parsedMapFromContent(fieldsList, message);
        map.put("rowNumber", tudfFileContent.getPayload().getRowNumber());
        map.put("recordType", fetchRecordType(recordTypeSymbol));
        return map;
    }

    @Override
    public @NonNull FileParserType getType() {
        return FileParserType.TUDF;
    }

    private static Character getRecordTypeSymbol(String content, TudfFileParserConfig fileParserConfig) {

        Character recordTypeSymbol = content.charAt(fileParserConfig.getRecordIdentifierIndex() - 1);

        if (!fileParserConfig.getIdentifiers().containsKey(recordTypeSymbol)) {
            log.error("Identifier Not Found for {}, file content: {}", recordTypeSymbol, content);
        }

        return recordTypeSymbol;
    }

    private static Map<String, Object> parsedMapFromContent(List<FieldMapping> fieldsList, String content) {
        return fieldsList.stream()
                .collect(Collectors.toMap(ClassUtils.unary(), field -> parse(field, content)))
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(entry -> entry.getKey().getFieldName(), Map.Entry::getValue));
    }

    private static String parse(FieldMapping field, String content) {
        if (field.getEnd() < content.length() && field.getStart() != 0 && field.getEnd() != 0) {
            String parsedString = content.substring(field.getStart() - 1, field.getEnd() - 1).trim();
            if (!parsedString.isEmpty()) {
                return parsedString;
            }
            return StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    private TudfFileParserConfig getTudfFileParserConfig(
            FileContentParsingRequest request) throws UnsupportedFileException {

        return (TudfFileParserConfig)
                fileConfig.getParserConfig(request.getFileType());
    }

    private String fetchRecordType(Character recordTypeSymbol) throws TudfFileContentParsingException {
        return Arrays.stream(RecordType.values())
                .filter(record -> record.getRecordTypeSymbol() == recordTypeSymbol)
                .findFirst()
                .map(RecordType::getRecordType)
                .orElseThrow(TudfFileContentParsingException::create);
    }

    @Getter
    @RequiredArgsConstructor
    private enum RecordType {

        TRANSACTION_DETAILS("TransactionDetails", 'D'),
        CUSTOMER_DETAILS("CustomerDetails", 'B');

        private final String recordType;
        private final Character recordTypeSymbol;

    }
}
