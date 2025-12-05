package com.samsepiol.file.nexus.sftp;


import com.jcraft.jsch.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JschLogger implements Logger {
    @Override
    public boolean isEnabled(int level) {
        // Enable all log levels
        return true;
    }

    @Override
    public void log(int level, String message) {
        switch (level) {
            case DEBUG:
                log.debug(message);
                break;
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
            case FATAL:
                log.error(message);
                break;
            default:
                log.info("UNKNOWN: {}", message);
        }
    }
}