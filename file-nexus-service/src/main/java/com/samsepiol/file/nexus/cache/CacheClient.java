package com.samsepiol.file.nexus.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClient {

    // TODO remove
//    private final RedisClient redisClient;

    public <T> T get(String key, Class<T> cls) {
        T response = null;
        try {
            log.info("Getting key in cache : {}", key);
//            response = redisClient.get(key, cls);
            return null;
        } catch (Exception ex) {
            log.error("Error fetching key:{} from redis with error = ", key, ex);
        }

        if (Objects.nonNull(response)) {
            log.info("Cache hit {}", key);
        } else {
            log.info("Cache miss {}", key);
        }
        return response;
    }

    public void setWithTtl(String key, Object value, int ttl, TimeUnit timeUnit) {
        try {
            log.info("Setting key in cache : {}", key);
//            redisClient.setWithTtl(key, value, ttl, timeUnit);
        } catch (Exception ex) {
            log.error("Failed to set key:{}, value:{} with error = ", key, value, ex);
        }
    }
}
