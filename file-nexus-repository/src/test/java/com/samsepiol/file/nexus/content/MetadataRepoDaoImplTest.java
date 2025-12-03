package com.samsepiol.file.nexus.content;

import com.samsepiol.file.nexus.enums.MetadataStatus;
import com.samsepiol.file.nexus.metadata.impl.MetadataRepositoryImpl;
import com.samsepiol.file.nexus.metadata.models.FetchMetaDataEntityRequest;
import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.mongo.helper.IMongoDbHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static com.samsepiol.file.nexus.enums.MetadataStatus.COMPLETED;
import static com.samsepiol.file.nexus.enums.MetadataStatus.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(profiles = "test")
public class MetadataRepoDaoImplTest extends BasePersistenceTest {
    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.0"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        setupDynamicPropertyRegistry(registry, mongoDBContainer.getConnectionString());
    }

    @Autowired
    private MetadataRepositoryImpl metadataRepository;

    @Autowired
    private IMongoDbHelper mongoDbHelper;

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.close();
    }

    @Test
    void saveMetadataSuccessTest() {
        MetadataEntity metadata = getMetadataEntityWithStatus(PENDING);
        metadataRepository.save(metadata);
        assertEquals(metadata, getMetadataEntity(metadataRepository.fetch(metadata.getId())));
    }

    @Test
    void updateMetadataSuccessTest() {
        var metadata = getMetadataEntityWithStatus(COMPLETED);

        metadataRepository.update(metadata);
        assertEquals(metadata, getMetadataEntity(metadataRepository.fetch(metadata.getId())));
    }

    @Test
    void fetchByIdSuccessTest() {
        var metadata = getMetadataEntityWithStatus(COMPLETED);

        assertEquals(metadata, getMetadataEntity(metadataRepository.fetch(metadata.getId())));
    }

    @Test
    void fetchByRequestSuccessTest() {
        var metadata = getMetadataEntityWithStatus(COMPLETED);

        FetchMetaDataEntityRequest request = FetchMetaDataEntityRequest.builder()
                .fileType("STATEMENT")
                .date("20240207")
                .build();

        assertEquals(metadata, metadataRepository.fetch(request).get(0));
    }

    private MetadataEntity getMetadataEntity(Optional<MetadataEntity> optionalEntity) {
        return optionalEntity.orElse(null);
    }

    private static MetadataEntity getMetadataEntityWithStatus(MetadataStatus status) {
        return MetadataEntity.builder()
                .id("STATEMENT_20240207_0")
                .fileName("STATEMENT_20240207_0")
                .fileType("STATEMENT")
                .createdAt(124L)
                .updatedAt(456L)
                .date("20240207")
                .status(status)
                .build();
    }
}
