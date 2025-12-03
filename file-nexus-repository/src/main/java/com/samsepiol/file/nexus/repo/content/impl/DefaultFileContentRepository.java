package com.samsepiol.file.nexus.repo.content.impl;


import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.samsepiol.file.nexus.repo.constants.RepositoryConstants;
import com.samsepiol.file.nexus.repo.content.FileContentRepository;
import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentFetchRepositoryRequest;
import com.samsepiol.file.nexus.repo.content.models.request.FileContentSaveRepositoryRequest;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseReadException;
import com.samsepiol.file.nexus.repo.exception.FileContentDatabaseWriteException;
import com.samsepiol.library.mongo.Repository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.stream.Stream;

/**
 * Mongo DB based implementation of File Content Repository
 *
 * @author satyajitroy
 */
@Slf4j
@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class DefaultFileContentRepository implements FileContentRepository {
    private static final String COLLECTION_NAME = "file_contents";
    private final Repository repository;

    @Override
    public @NonNull List<FileContent> fetch(@NonNull FileContentFetchRepositoryRequest request) throws FileContentDatabaseReadException {
        try {
            var query = prepareQuery(request);
            return repository.findAll(COLLECTION_NAME, query, FileContent.class);
        } catch (MongoException exception) {
            log.error("File contents DB read failure: ", exception);
            throw FileContentDatabaseReadException.create();
        }

    }

    @NonNull
    @Override
    public List<FileContent> save(@NonNull FileContentSaveRepositoryRequest request) throws FileContentDatabaseWriteException {
        try {
            // TODO use bulk insert
            request.getEntities()
                    .forEach(fileContent -> repository.insert(COLLECTION_NAME, fileContent, FileContent.class));
            return request.getEntities();
        } catch (MongoException exception) {
            log.error("File contents DB write failure: ", exception);
            throw FileContentDatabaseWriteException.create();
        }
    }

    private static Bson prepareQuery(FileContentFetchRepositoryRequest request) {
        var filters = request.getQuery().entrySet().stream()
                .map(entry -> Filters.eq(entry.getKey(), entry.getValue()));

        var fileIdFilter = Filters.in(RepositoryConstants.FileContentEntityConstants.FILE_ID, request.getFileIds());
        return Filters.and(Stream.concat(filters, Stream.of(fileIdFilter)).toList());
    }

}
