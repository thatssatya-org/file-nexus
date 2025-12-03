package com.samsepiol.file.nexus.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassUtils {

    public static <T> Function<T, T> unary() {
        return x -> x;
    }

    public static <T> Consumer<T> emptyConsumer() {
        return param -> {};
    }
}
