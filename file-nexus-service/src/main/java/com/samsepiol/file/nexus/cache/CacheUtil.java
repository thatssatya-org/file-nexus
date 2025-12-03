package com.samsepiol.file.nexus.cache;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheUtil {

    public static final String STATEMENT_CACHE_KEY = "statement-cache";

    public static String getStatementCacheKey(String accountNumber, String fileName){
        return STATEMENT_CACHE_KEY + "-" + accountNumber + "-" + fileName;
    }
}
