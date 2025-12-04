package com.samsepiol.file.nexus;

import com.redis.testcontainers.RedisContainer;
import com.samsepiol.file.nexus.config.BaseControllerTestConfig;
import com.samsepiol.library.cache.Cache;
import com.samsepiol.library.mongo.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    protected static MongoDBContainer getMongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    }

    protected static RedisContainer getRedisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:latest"));
    }


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected Repository mongoDb;

    @Autowired
    protected Cache<String, Object> redisClient;

    // TODO
//    @MockBean
//    protected TemporalConnection temporalConnection;
//
//
//    @MockBean
//    protected IConsumerClient consumerClient;
//
//    @MockBean
//    protected IProducerClient producerClient;
//
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

    protected synchronized void loadDB(Map<String, Map<String, Object>> map) {
        if (!dbLoaded) {
            // TODO
//            map.forEach((collectionName, bulkSaveData) -> mongoDb.bulkSave(collectionName, bulkSaveData));
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
