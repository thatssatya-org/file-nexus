package com.samsepiol.file.nexus.transfer.storage;


import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

@Data
@Builder
@Slf4j
public class CloseableInputStream extends InputStream implements Closeable {
    private final InputStream delegate;

    public CloseableInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public void close() throws IOException {
        log.info("Closing fileStream");
        delegate.close();
    }
}