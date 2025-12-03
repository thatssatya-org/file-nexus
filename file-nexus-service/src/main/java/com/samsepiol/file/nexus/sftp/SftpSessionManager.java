package com.samsepiol.file.nexus.sftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.samsepiol.file.nexus.content.exception.SftpConnectionException;
import com.samsepiol.file.nexus.transfer.storage.config.SftpConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static com.samsepiol.file.nexus.models.enums.Error.SFTP_SESSION_CREATION_FAILURE;

@Slf4j
public class SftpSessionManager {

    private static Session session;
    private final SftpConfig sftpConfig;

    public SftpSessionManager(SftpConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    public Session get() throws SftpConnectionException {
        try {
            if (session == null || !session.isConnected()) {
                JSch jsch = new JSch();
                jsch.setLogger(new JschLogger());
                String privateKey = sftpConfig.getPrivateKey();
                byte[] privateKeyBytes = Base64.decodeBase64(privateKey);
                jsch.addIdentity(sftpConfig.getAliasName(), privateKeyBytes, null, sftpConfig.getPassphrase().getBytes(StandardCharsets.UTF_8));
                session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                config.put("PreferredAuthentications", "publickey"); // Added authentication method for server
                session.setConfig(config);
                session.setServerAliveInterval(30000); // Send keep-alive every 30 seconds
                session.connect(3_00_000); // keep connection active for 5 mins
                log.info("Session connected successfully");
                // TODO: add metric for successful sftp connection
            }
            return session;
        } catch (Exception e) {
            log.error("Error creating jsch session: {}", e.getMessage(), e);
            throw SftpConnectionException.create(SFTP_SESSION_CREATION_FAILURE);
        }
    }

    public void close() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}
