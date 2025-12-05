package com.samsepiol.file.nexus.storage.service;

import com.samsepiol.file.nexus.models.transfer.enums.StorageType;
import com.samsepiol.file.nexus.storage.hook.StorageHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HookFactory {

    private final Map<StorageType, StorageHook> storageHookMap;

    @Autowired
    public HookFactory(
            List<StorageHook> storageHooks
    ) {
        storageHookMap = storageHooks.stream().collect(Collectors.toMap(StorageHook::getStorageType, Function.identity()));
    }

    public StorageHook getHook(StorageType type) {
        return storageHookMap.get(type);
    }
}
