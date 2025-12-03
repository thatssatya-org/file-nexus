package com.samsepiol.file.nexus.api;

import com.samsepiol.file.nexus.BaseControllerTest;
import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.models.content.request.FileContentsQuery;
import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.library.core.exception.SerializationException;
import com.samsepiol.library.core.util.SerializationUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileContentControllerTest extends BaseControllerTest {

    @Container
    private static final MongoDBContainer MONGO_DB_CONTAINER = newMongoDbContainer();

    private static final String BASE_MAPPING = "/v1/files/TEST_FILE";
    private static final String GET_FILE_CONTENTS_API = BASE_MAPPING + "/contents";
    private static final String FILE_TYPE_PATH_VARIABLE = "$fileType";
    private static final String TEST_FILE_ID = "test_file_id";
    private static final String TEST_FILE_TYPE = "TEST_FILE";
    private static final String TEST_FILE_DATE = "20241028";

    @Test
    void testGetFileContents() throws Exception {
        var requestPayload = SerializationUtil.convertToString(FileContentsQuery.builder()
                .date(TEST_FILE_DATE)
                .filters(Map.of("TEST_FILE_COLUMN_1", "VALUE_1"))
                .build());

        postAction(GET_FILE_CONTENTS_API, requestPayload, FILE_TYPE_PATH_VARIABLE, TEST_FILE_TYPE)
                .andExpect(status().isOk())
                .andReturn();
    }

    private static Map<String, Object> metaDataWithCompletedStatus() {
        return Map.of(TEST_FILE_ID, MetadataEntity.builder()
                .id(TEST_FILE_ID)
                .fileType(TEST_FILE_TYPE)
                .date(TEST_FILE_DATE)
                .status(MetadataStatus.COMPLETED)
                .createdAt(123456L)
                .updatedAt(2345678L)
                .fileName(TEST_FILE_ID)
                .build());
    }

    private static Map<String, Object> fileContents() throws SerializationException {
        return Map.of(TEST_FILE_ID + "1", FileContent.builder()
                .id(TEST_FILE_ID + "1")
                .fileId(TEST_FILE_ID)
                .createdAt(123456L)
                .updatedAt(2345678L)
                .rowNumber("1")
                .index1("VALUE_1")
                .content(SerializationUtil.convertToString(Map.of("VALUE_1", "hehe")))
                .build());
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        properties(MONGO_DB_CONTAINER, registry);
    }

    @BeforeEach
    public void setUp() throws SerializationException {
        var dbData = Map.of(
                "file_metadata", metaDataWithCompletedStatus(),
                "file_contents", fileContents());

        loadDB(dbData);
    }

    @BeforeAll
    static void start() {
        MONGO_DB_CONTAINER.start();
    }

    @AfterAll
    static void stop() {
        MONGO_DB_CONTAINER.close();
    }
}
