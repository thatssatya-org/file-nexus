package com.samsepiol.file.nexus.transfer.storage;


import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class CloseableSftpInputStream extends CloseableInputStream implements Closeable {
    private final ChannelSftp channel;

    public CloseableSftpInputStream(InputStream delegate, ChannelSftp channel) {
        super(delegate);
        this.channel = channel;
    }

    @Override
    public void close() throws IOException {
        log.info("Closing fileStream");
        super.close();
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
            log.info("Channel disconnected");
        }
    }
}