package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.content.config.FileSchemaConfig;
import com.samsepiol.file.nexus.content.exception.UnsupportedFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/file/configs")
@RequiredArgsConstructor
public class FileConfigController {
    private final FileSchemaConfig fileSchemaConfig;

    @GetMapping("/{fileType}/schema")
    public ResponseEntity<FileSchemaConfig.SchemaConfig> getSchemaConfig(@PathVariable String fileType) {
        try {
            var schemaConfig = fileSchemaConfig.getOptionalSchemaConfig(fileType);
            return ResponseEntity.ok(schemaConfig);
        } catch (UnsupportedFileException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/schema")
    public ResponseEntity<Map<String, FileSchemaConfig.SchemaConfig>> getSchemaConfig() {
        var schemaConfig = fileSchemaConfig.getFileConfigMap();
        return ResponseEntity.ok(schemaConfig);
    }
}
