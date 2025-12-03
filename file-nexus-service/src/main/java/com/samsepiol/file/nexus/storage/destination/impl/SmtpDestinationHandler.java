package com.samsepiol.file.nexus.storage.destination.impl;

import com.samsepiol.file.nexus.storage.config.destination.SmtpConfig;
import com.samsepiol.file.nexus.storage.destination.DestinationHandler;
import com.samsepiol.file.nexus.storage.destination.DestinationType;
import com.samsepiol.file.nexus.storage.destination.models.SendFileRequestDto;
import com.samsepiol.file.nexus.storage.models.FileInfo;
import com.samsepiol.file.nexus.storage.transformer.FileTransformer;
import com.samsepiol.file.nexus.storage.transformer.FileTransformer.TransformationResult;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

/**
 * Implementation of DestinationHandler for SMTP destinations.
 * Handles sending files via email using JavaMail API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpDestinationHandler implements DestinationHandler {

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 MB (typical email limit)
    private static final String DEFAULT_SUBJECT = "File Transfer: ";
    private static final String DEFAULT_BODY = "Please find the attached file.";

    private static void addRecipients(String smtpConfig, MimeMessage message, Message.RecipientType to) throws MessagingException {
        String[] recipients = smtpConfig.split(",");
        InternetAddress[] addresses = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addresses[i] = new InternetAddress(recipients[i].trim());
        }
        message.setRecipients(to, addresses);
    }

    @Override
    public DestinationType getType() {
        return DestinationType.SMTP;
    }

    @Override
    public boolean sendFile(SendFileRequestDto sendFileRequestDto) {
        if (!(sendFileRequestDto.getConfig() instanceof SmtpConfig smtpConfig)) {
            log.error("Invalid config type for SMTP destination: {}", sendFileRequestDto.getConfig().getClass().getName());
            return false;
        }

        FileInfo fileInfo = sendFileRequestDto.getFileInfo();
        String fileName = sendFileRequestDto.getFileName();

        if (fileInfo.getSize() > MAX_FILE_SIZE) {
            log.error("File {} exceeds maximum size of {} bytes", fileName, MAX_FILE_SIZE);
            return false;
        }

        if (fileInfo.getSize() == 0) {
            log.warn("File {} is empty, skipping email", fileName);
            return false;
        }

        try (
            InputStream fileStream = sendFileRequestDto.getStorageHook()
                    .getFileAsStream(fileInfo.getFileKey(), 0L)
        ) {
            TransformationResult transformationResult = FileTransformer.transformFile(
                    fileStream, fileName, smtpConfig.getTransformerType());

            byte[] fileContent = transformationResult.content();
            fileName = transformationResult.fileName();

            log.info("Sending file {} ({} bytes) via email to {}", fileName, fileContent.length, smtpConfig.getTo());

            return sendEmail(smtpConfig, fileName, fileContent, fileInfo);
        } catch (Exception e) {
            log.error("Unexpected error while processing file {}: {}", fileName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sends an email with the file as an attachment.
     */
    private boolean sendEmail(SmtpConfig smtpConfig, String fileName, byte[] fileContent, FileInfo fileInfo) {
        try {
            Session session = createMailSession(smtpConfig);
            MimeMessage message = new MimeMessage(session);

            if (smtpConfig.getFrom() != null && !smtpConfig.getFrom().trim().isEmpty()) {
                message.setFrom(new InternetAddress(smtpConfig.getFrom()));
            } else {
                log.error("From address is required for SMTP configuration");
                return false;
            }

            if (smtpConfig.getTo() != null && !smtpConfig.getTo().trim().isEmpty()) {
                addRecipients(smtpConfig.getTo(), message, Message.RecipientType.TO);
            } else {
                log.error("To address is required for SMTP configuration");
                return false;
            }

            if (smtpConfig.getCc() != null && !smtpConfig.getCc().trim().isEmpty()) {
                addRecipients(smtpConfig.getCc(), message, Message.RecipientType.CC);
            }

            String subject = smtpConfig.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                subject = DEFAULT_SUBJECT + fileName;
            }
            subject = replaceVariables(subject, fileInfo);
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            String body = smtpConfig.getBody();
            if (body == null || body.trim().isEmpty()) {
                body = DEFAULT_BODY;
            }
            body = replaceVariables(body, fileInfo);

            textPart.setText(body);
            multipart.addBodyPart(textPart);

            MimeBodyPart attachmentPart = new MimeBodyPart();

            String contentType = guessContentType(fileName);
            DataSource dataSource = new ByteArrayDataSource(fileContent, contentType);
            attachmentPart.setDataHandler(new DataHandler(dataSource));
            attachmentPart.setFileName(fileName);

            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);
            log.info("Successfully sent email with attachment {} to {}", fileName, smtpConfig.getTo());
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment {}: {}", fileName, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while sending email with attachment {}: {}", fileName, e.getMessage(), e);
            return false;
        }
    }

    private String replaceVariables(String subject, FileInfo fileInfo) {
        /**
         * there are some variables like DATE, in {{DATE}}, date date time from file
         */
        if (subject == null) {
            return null;
        }
        return subject.replace("{{DATE}}", fileInfo.getLastModified().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString())
                .replace("{{FILENAME}}", fileInfo.getFileKey())
                .replace("{{SIZE}}", String.valueOf(fileInfo.getSize()));
    }

    /**
     * Creates a mail session with the given SMTP configuration.
     */
    private Session createMailSession(SmtpConfig smtpConfig) {
        Properties props = new Properties();

        // SMTP server configuration
        props.put("mail.smtp.host", smtpConfig.getHost());
        props.put("mail.smtp.port", smtpConfig.getPort() != null ? smtpConfig.getPort().toString() : "587");

        // SSL/TLS configuration
        if (smtpConfig.getSsl() != null && smtpConfig.getSsl()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        // Connection settings
        props.put("mail.smtp.connectiontimeout", "30000"); // 30 seconds
        props.put("mail.smtp.timeout", "30000"); // 30 seconds
        props.put("mail.smtp.writetimeout", "30000"); // 30 seconds

        // Authentication
        if (smtpConfig.getUsername() != null && !smtpConfig.getUsername().trim().isEmpty()) {
            props.put("mail.smtp.auth", "true");

            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpConfig.getUsername(), smtpConfig.getPassword());
                }
            });
        } else {
            props.put("mail.smtp.auth", "false");
            return Session.getInstance(props);
        }
    }

    /**
     * Guess the content type based on file extension.
     */
    private String guessContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }

        String lowerFileName = fileName.toLowerCase();

        if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerFileName.endsWith(".csv")) {
            return "text/csv";
        } else if (lowerFileName.endsWith(".xml")) {
            return "application/xml";
        } else if (lowerFileName.endsWith(".json")) {
            return "application/json";
        } else if (lowerFileName.endsWith(".zip")) {
            return "application/zip";
        } else if (lowerFileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (lowerFileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (lowerFileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lowerFileName.endsWith(".doc")) {
            return "application/msword";
        }

        return "application/octet-stream";
    }
}
