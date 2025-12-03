package com.samsepiol.file.nexus.advice;

import com.samsepiol.file.nexus.content.exception.DiscardedFileRequestedException;
import com.samsepiol.file.nexus.content.exception.FileNotReadyForConsumptionException;
import com.samsepiol.file.nexus.exception.FileNexusExceptionHandlingService;
import com.samsepiol.file.nexus.exception.checked.FileNexusException;
import com.samsepiol.file.nexus.exception.unchecked.FileNexusRuntimeException;
import com.samsepiol.file.nexus.models.content.response.FileContentsResponse;
import com.samsepiol.file.nexus.utils.DateTimeUtils;
import com.samsepiol.helper.exception.ErrorResponse;
import com.samsepiol.helper.exception.GenericException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {
    private final FileNexusExceptionHandlingService exceptionHandlingService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(getErrorResponse(ex));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(getErrorResponse(ex));
    }

    @ExceptionHandler(DiscardedFileRequestedException.class)
    public ResponseEntity<FileContentsResponse> handleDiscardedFileRequestedException(DiscardedFileRequestedException exception) {

        var discardedFileContentsResponse =
                FileContentsResponse.discarded(exception.getFileType(), DateTimeUtils.toYYYYMMDD(exception.getDate()));

        return ResponseEntity.ok().body(discardedFileContentsResponse);
    }

    @ExceptionHandler(FileNotReadyForConsumptionException.class)
    public ResponseEntity<FileContentsResponse> handleFileNotReadyForConsumptionException(FileNotReadyForConsumptionException exception) {

        var pendingFileContentsResponse =
                FileContentsResponse.pending(exception.getFileType(), DateTimeUtils.toYYYYMMDD(exception.getDate()));

        return ResponseEntity.ok().body(pendingFileContentsResponse);
    }

    @ExceptionHandler(FileNexusException.class)
    public ResponseEntity<ErrorResponse> handleFileNexusException(FileNexusException exception) {

        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(exceptionHandlingService.handleException(exception));
    }

    @ExceptionHandler(FileNexusRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleFileNexusRuntimeException(FileNexusRuntimeException exception) {

        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(exceptionHandlingService.handleException(exception));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> handleGenericException(GenericException exception) {

        return ResponseEntity
                .internalServerError()
                .body(exceptionHandlingService.handleException(exception));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {

        return ResponseEntity
                .internalServerError()
                .body(exceptionHandlingService.handleException(exception));
    }

    private static ErrorResponse getErrorResponse(Exception ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .displayMessage(ex.getLocalizedMessage())
                .build();
    }

}
