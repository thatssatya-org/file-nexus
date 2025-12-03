package com.samsepiol.file.nexus.metadata.codec.supplier;

import com.samsepiol.file.nexus.repo.content.entity.MetadataEntity;
import com.samsepiol.library.mongo.codec.CodecSupplier;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileMetadataRepositoryCodecSupplier implements CodecSupplier {

    @Override
    public @NonNull List<Class<?>> getManagedClasses() {
        return List.of(MetadataEntity.class);
    }
}
