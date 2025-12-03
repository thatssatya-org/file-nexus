package com.samsepiol.file.nexus.storage.destination.impl;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.samsepiol.file.nexus.storage.config.destination.SftpConfig;
import com.samsepiol.file.nexus.storage.destination.DestinationHandler;
import com.samsepiol.file.nexus.storage.destination.DestinationType;
import com.samsepiol.file.nexus.storage.destination.models.SendFileRequestDto;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Implementation of DestinationHandler for SFTP destinations.
 * Handles sending files via SFTP using JSch library.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SftpDestinationHandler implements DestinationHandler {

    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final int SESSION_TIMEOUT = 300000; // 5 minutes

    @Override
    public DestinationType getType() {
        return DestinationType.SFTP;
    }

    @Override
    public boolean sendFile(SendFileRequestDto sendFileRequestDto) {
        if (!(sendFileRequestDto.getConfig() instanceof SftpConfig sftpConfig)) {
            log.error("Invalid config type for SFTP destination: {}", sendFileRequestDto.getConfig().getClass().getName());
            return false;
        }

        Session session = null;
        ChannelSftp channel = null;

        FileInfo fileInfo = sendFileRequestDto.getFileInfo();

        try {
            InputStream fileContentStream = sendFileRequestDto.getStorageHook().getFileAsStream(
                    fileInfo.getFileKey(),
                    0L
            );
            String fileName = sendFileRequestDto.getFileName();

            if (fileContentStream == null) {
                log.warn("File content stream is null for SFTP upload, skipping. {}", fileName);
                return false;
            }

            // Check if stream is empty by trying to read a byte without consuming it
            // This requires markSupported() and reset()
            if (fileContentStream.markSupported()) {
                fileContentStream.mark(1);
                if (fileContentStream.read() == -1) {
                    log.warn("File {} stream is empty, skipping upload", fileName);
                    return false;
                }
                fileContentStream.reset(); // Reset to original position
            } else {
                log.warn("InputStream does not support mark/reset. Cannot reliably check for emptiness without consuming data.");
            }


            String remoteDirectory = sftpConfig.getRemoteDirectory();
            if (remoteDirectory == null || remoteDirectory.isEmpty()) {
                remoteDirectory = "/";
            }

            // Ensure the remote directory ends with /
            if (!remoteDirectory.endsWith("/")) {
                remoteDirectory += "/";
            }

            String remotePath = remoteDirectory + fileName;

            log.info("Uploading file via SFTP: {} to {}@{}:{}{}",
                    fileName, sftpConfig.getUsername(), sftpConfig.getHost(),
                    sftpConfig.getPort(), remotePath);

            session = createSftpSession(sftpConfig);
            session.connect(CONNECTION_TIMEOUT);
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();


            createRemoteDirectoryIfNeeded(channel, remoteDirectory);

            channel.put(fileContentStream, remotePath); // Use the InputStream directly
            log.info("Successfully uploaded file {} to SFTP server", fileName); // Removed byte count as it's not easily available from stream
            return true;

        } catch (Exception e) {
            log.error("Failed to upload file {} via SFTP: {}", sendFileRequestDto.getFileName(), e.getMessage(), e);
            return false;
        } finally {
            if (channel != null && channel.isConnected()) {
                try {
                    channel.disconnect();
                } catch (Exception e) {
                    log.warn("Error closing SFTP channel: {}", e.getMessage());
                }
            }

            if (session != null && session.isConnected()) {
                try {
                    session.disconnect();
                } catch (Exception e) {
                    log.warn("Error closing SFTP session: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Creates an SFTP session with the given configuration.
     */
    private Session createSftpSession(SftpConfig sftpConfig) throws Exception {
        JSch jsch = new JSch();

        // Set up authentication
        if (sftpConfig.getPrivateKey() != null && !sftpConfig.getPrivateKey().trim().isEmpty()) {
            // Use private key authentication
            byte[] privateKeyBytes = Base64.decodeBase64(sftpConfig.getPrivateKey());
            byte[] passphrase = sftpConfig.getPassphrase() != null ?
                    sftpConfig.getPassphrase().getBytes(StandardCharsets.UTF_8) : null;
            jsch.addIdentity("sftp-key", privateKeyBytes, null, passphrase);
            log.debug("Using private key authentication for SFTP");
        }

        Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());

        if (sftpConfig.getPassword() != null && !sftpConfig.getPassword().trim().isEmpty()) {
            session.setPassword(sftpConfig.getPassword());
        }

        Properties sessionConfig = new Properties();
        sessionConfig.put("StrictHostKeyChecking",
                sftpConfig.getStrictHostKeyChecking() != null && sftpConfig.getStrictHostKeyChecking() ? "yes" : "no");
        sessionConfig.put("PreferredAuthentications",
                sftpConfig.getPrivateKey() != null ? "publickey,password" : "password,publickey");
        session.setConfig(sessionConfig);
        session.setTimeout(SESSION_TIMEOUT);

        return session;
    }

    /**
     * Creates the remote directory if it doesn't exist.
     */
    private void createRemoteDirectoryIfNeeded(ChannelSftp channel, String remoteDirectory) {
        try {
            // Try to change to the directory first
            channel.cd(remoteDirectory);
            log.debug("Remote directory {} already exists", remoteDirectory);
        } catch (Exception e) {
            // Directory doesn't exist, create it
            try {
                log.info("Creating remote directory: {}", remoteDirectory);

                // Split path and create directories recursively
                Path path = Paths.get(remoteDirectory);
                String currentPath = "/";

                for (Path part : path) {
                    if (!part.toString().isEmpty()) {
                        currentPath = Paths.get(currentPath, part.toString()).toString();
                        try {
                            channel.mkdir(currentPath);
                            log.debug("Created directory: {}", currentPath);
                        } catch (Exception mkdirEx) {
                            // Directory might already exist, check if we can cd to it
                            try {
                                channel.cd(currentPath);
                            } catch (Exception cdEx) {
                                log.error("Failed to create or access directory {}: {}", currentPath, mkdirEx.getMessage());
                                throw mkdirEx;
                            }
                        }
                    }
                }
            } catch (Exception createEx) {
                log.error("Failed to create remote directory {}: {}", remoteDirectory, createEx.getMessage());
                throw new RuntimeException("Failed to create remote directory: " + remoteDirectory, createEx);
            }
        }
    }
}
