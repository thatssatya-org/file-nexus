package com.samsepiol.file.nexus.lock;

import lombok.NonNull;

/**
 * Service to execute given runnable with lock
 */
public interface ExecuteWithLockService {

    void execute(@NonNull Runnable runnable, @NonNull String lockId);
}
