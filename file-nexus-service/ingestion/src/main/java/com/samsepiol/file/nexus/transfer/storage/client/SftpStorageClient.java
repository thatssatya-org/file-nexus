package com.samsepiol.file.nexus.transfer.storage.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.samsepiol.file.nexus.transfer.exception.SftpConnectionException;
import com.samsepiol.file.nexus.transfer.exception.SftpServiceException;
import com.samsepiol.file.nexus.transfer.exception.UnImplementedException;
import com.samsepiol.file.nexus.models.enums.Error;
import com.samsepiol.file.nexus.sftp.SftpSessionManager;
import com.samsepiol.file.nexus.transfer.storage.CloseableInputStream;
import com.samsepiol.file.nexus.transfer.storage.CloseableSftpInputStream;
import com.samsepiol.file.nexus.transfer.storage.config.SftpConfig;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.samsepiol.file.nexus.models.enums.Error.IMPLEMENTATION_PENDING;
import static com.samsepiol.file.nexus.models.enums.Error.SFTP_CHANNEL_CREATION_FAILURE;

@Slf4j
public class SftpStorageClient implements IStorageClient {
    private final SftpSessionManager sessionManager;
    private final SftpConfig config;

    public SftpStorageClient(String host, String privateKey, String passphrase, String aliasName, Integer port, String username, String rootDir) {
        this.config = SftpConfig.builder().host(host).privateKey(privateKey)
                .passphrase(passphrase).aliasName(aliasName).port(port).username(username)
                .rootDir(rootDir).build();
        this.sessionManager = new SftpSessionManager(this.config);

    }

    @Override
    public List<String> getAllFiles(String bucketName) {
        ChannelSftp channel = null;
        try {
            channel = getChannel();
            if(StringUtils.isNotBlank(config.getRootDir())){
                //TODO: check if we want to read all files recursively
                Vector<ChannelSftp.LsEntry> files = channel.ls(config.getRootDir());
                return files.stream()
                        .filter(file -> !file.getAttrs().isDir())
                        .map(file -> config.getRootDir() + file.getFilename())
                        .collect(Collectors.toUnmodifiableList());
            }
            return Collections.EMPTY_LIST;
        } catch (SftpConnectionException e) {
            throw e;
        } catch (Exception e){
            log.error("Error fetching files from SFTP server: {}", e.getMessage());
            throw SftpConnectionException.create(Error.SFTP_FILE_FETCH_FAILED);
        } finally {
            closeChannel(channel);
            closeSession();
        }
    }

    @Override
    public void uploadFile(String bucketName, String remotePath, InputStream inputStream) {
        throw UnImplementedException.create(IMPLEMENTATION_PENDING);
    }

    @Override
    public CloseableInputStream getFileStream(String bucketName, String remotePath) {
        log.info("Trying to get stream for file: {}", remotePath);
        try {
            ChannelSftp channel = getChannel();
            InputStream inputStream = channel.get(remotePath);
            return new CloseableSftpInputStream(inputStream, channel);
        } catch(Exception e){
            log.error("Error getting file stream for filePath : {} message: {}", remotePath, e.getMessage());
            throw SftpServiceException.create(Error.SFTP_STREAMING_ERROR);
        }
    }


    @Override
    public void deleteFile(String bucketName, String remotePath) {
        throw UnImplementedException.create(IMPLEMENTATION_PENDING);
    }

    @Override
    public void rename(String bucketName, String remoteFilePath, String newFilePath) {
        ChannelSftp channel = null;
        log.info("Trying to rename file: {} with path {}", remoteFilePath, newFilePath);
        try {
            channel = getChannel();
            // move the file
            channel.rename(remoteFilePath, newFilePath);
        } catch (SftpConnectionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error renaming file : {} with error : {}", remoteFilePath, e.getMessage());
            throw SftpServiceException.create(Error.SFTP_RENAME_FILE_COMMAND_FAILED);
        } finally {
            closeChannel(channel);
            closeSession();
        }
    }

    @Override
    public Boolean fileExists(String bucketName, String remoteFilePath) {
        ChannelSftp channel = null;
        try {
            channel = getChannel();
            channel.lstat(remoteFilePath); // This will throw an exception if the file doesn't exist
            return true;
        } catch(SftpException e){
            if(e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                log.error("Error file does not exist : {} with error: {}", remoteFilePath, e.getMessage());
                return false;
            }
            log.error("Error checking file exists : {} with error: {}", remoteFilePath, e.getMessage());
            throw SftpServiceException.create(Error.SFTP_FILE_NOT_FOUND_ERROR);
        } catch (Exception e) {
            log.error("Error checking file : {} with error: {}", remoteFilePath, e.getMessage());
            throw SftpServiceException.create(Error.SFTP_FILE_NOT_FOUND_ERROR);
        } finally {
            closeChannel(channel);
            closeSession();
        }
    }

    public ChannelSftp getChannel() {
        try {
            Session session = sessionManager.get();
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            log.info("SFTP channel connected with id: {}", channel.getId());
            return channel;
        } catch (SftpConnectionException e){
            throw e;
        } catch (Exception e){
            log.error("Error initiating session with SFTP server: {}", e.getMessage());
            throw SftpConnectionException.create(SFTP_CHANNEL_CREATION_FAILURE);
        }
    }


    public void closeChannel(ChannelSftp channel) {
        try {
            if (channel != null && channel.isConnected()) {
                log.info("Closing SFTP channel with id: {}", channel.getId());
                channel.disconnect();
            }
        } catch (Exception e) {
            log.warn("Close SFTP channel failing with error {}", e.getMessage());
        }
    }

    public void closeSession() {
        sessionManager.close();
    }
}
