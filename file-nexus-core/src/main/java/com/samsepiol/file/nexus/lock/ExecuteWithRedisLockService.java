package com.samsepiol.file.nexus.lock;

import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service executes runnable with redis distributed lock
 */
@Service
public class ExecuteWithRedisLockService implements ExecuteWithLockService {

    // TODO Redis lock
//    @ExecuteInLock(prefix = "FN-", lockIdKey = "arg.1")
    @Override
    public void execute(@NonNull Runnable runnable, @NonNull String lockId) {
        runnable.run();
    }
}
