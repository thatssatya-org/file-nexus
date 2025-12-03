package com.samsepiol.file.nexus.repo.content.codec.supplier;

import com.samsepiol.file.nexus.repo.content.entity.FileContent;
import com.samsepiol.library.mongo.codec.CodecSupplier;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileContentRepositoryCodecSupplier implements CodecSupplier {

    @Override
    public @NonNull List<Class<?>> getManagedClasses() {
        return List.of(FileContent.class);
    }
}
