package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.metadata.FileMetadataRepository;
import com.samsepiol.file.nexus.metadata.impl.MetadataRepositoryImpl;
import com.samsepiol.library.mongo.Repository;
import com.samsepiol.library.mongo.impl.DefaultRepository;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {FileMetadataRepository.class, MetadataRepositoryImpl.class,
        Repository.class, DefaultRepository.class, CompositeMeterRegistry.class})
@Testcontainers
@EnableConfigurationProperties
public abstract class BasePersistenceTest {

    @SpyBean
    protected Repository daoHelper;

    protected static void setupDynamicPropertyRegistry(DynamicPropertyRegistry registry, String connectionUrl) {
        registry.add("mongo-config.address", () -> connectionUrl.replace("mongodb://", ""));
        registry.add("mongo-config.database", () -> "test");
        registry.add("mongo-config.username", () -> "");
        registry.add("mongo-config.password", () -> "");
    }
}