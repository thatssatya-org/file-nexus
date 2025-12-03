package com.samsepiol.file.nexus;

import com.samsepiol.file.nexus.config.BaseControllerTestConfig;
import com.samsepiol.file.nexus.storage.service.ScheduleManagementService;
import com.samsepiol.filestoragesystem.client.IFileStorageClient;

import com.samsepiol.kafka.client.IConsumerClient;
import com.samsepiol.kafka.client.IProducerClient;
import com.samsepiol.mongo.helper.IMongoDbHelper;
import com.samsepiol.redis.client.UniRedisClient;
import com.samsepiol.temporal.impl.TemporalConnection;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = BaseControllerTestConfig.class)
@ActiveProfiles(profiles = "test")
public abstract class BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected IMongoDbHelper mongoDb;

    @MockBean
    protected TemporalConnection temporalConnection;

    @MockBean
    protected UniRedisClient redisClient;

    @MockBean
    protected MetricHelper metricHelper;

    @MockBean
    protected IConsumerClient consumerClient;

    @MockBean
    protected IProducerClient producerClient;

    @MockBean
    protected IFileStorageClient fileStorageClient;

    @MockBean
    protected ScheduleManagementService scheduleManagementService;

    protected MockMvc getMockMvc() {
        return mockMvc;
    }

    private Boolean dbLoaded = Boolean.FALSE;

    protected static void properties(MongoDBContainer mongoDBContainer, DynamicPropertyRegistry registry) {
        registry.add("mongo-config.address",
                () -> mongoDBContainer.getConnectionString().replace("mongodb://", ""));
        registry.add("mongo-config.database", () -> "test");
        registry.add("mongo-config.username", () -> "");
        registry.add("mongo-config.password", () -> "");
    }

    protected static @NonNull MongoDBContainer newMongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:5.0.0"));
    }

    protected synchronized void loadDB(Map<String, Map<String, Object>> map) {
        if (!dbLoaded) {
            map.forEach((collectionName, bulkSaveData) -> mongoDb.bulkSave(collectionName, bulkSaveData));
            dbLoaded = Boolean.TRUE;
        }
    }

    protected ResultActions postAction(String url, String payload, String paramName, String paramValue) throws Exception {
        return getMockMvc().perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param(paramName, paramValue)
                .content(payload));
    }
}
