package com.samsepiol.file.nexus.exception.impl;

import com.samsepiol.file.nexus.exception.FileNexusExceptionHandlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class ControllerAdviceExceptionHandlingService implements FileNexusExceptionHandlingService {
    private static final Integer ERROR_CODE_START = 9027001;
    private static final Integer ERROR_CODE_END = 9027999;
    private static final String LOG_PREFIX = "Controller Advice";

     // TODO
//    private final MetricHelper metricService;
//
//    @PostConstruct
//    protected void init() {
//        metricService.registerErrorRange(ERROR_CODE_START, ERROR_CODE_END);
//    }
//
//    @Override
//    public @NonNull ErrorResponse handleException(@NonNull FileNexusException exception) {
//        recordMetric(exception);
//        return errorResponse(exception.getErrorCode(), exception.getMessage());
//    }
//
//    @Override
//    public @NonNull ErrorResponse handleException(@NonNull FileNexusRuntimeException exception) {
//        recordMetric(exception);
//        return errorResponse(exception.getErrorCode(), exception.getMessage());
//    }
//
//    @Override
//    public @NonNull ErrorResponse handleException(@NonNull Exception exception) {
//        recordMetric(exception);
//        return errorResponse(Error.INTERNAL_SERVER_ERROR.getCode(), Error.INTERNAL_SERVER_ERROR.getMessage());
//    }
//
//    @Override
//    public @NonNull ErrorResponse handleException(@NonNull GenericException exception) {
//        recordMetric(exception);
//        return errorResponse(exception.getErrorCode(), exception.getMessage());
//    }
//
//    private void recordMetric(GenericException exception) {
//        ExceptionLoggingUtils.logException(LOG_PREFIX, exception);
//        metricService.recordErrorMetric(exception.getErrorCode(), exception.getMessage());
//    }
//
//    private void recordMetric(Exception exception) {
//        ExceptionLoggingUtils.logException(LOG_PREFIX, exception);
//        metricService.recordErrorMetric(Error.INTERNAL_SERVER_ERROR.getCode(), exception.getMessage());
//    }
//
//    private void recordMetric(FileNexusException exception) {
//        ExceptionLoggingUtils.logException(LOG_PREFIX, exception);
//        metricService.recordErrorMetric(exception.getErrorCode(), exception.getMessage());
//    }
//
//    private void recordMetric(FileNexusRuntimeException exception) {
//        ExceptionLoggingUtils.logException(LOG_PREFIX, exception);
//        metricService.recordErrorMetric(exception.getErrorCode(), exception.getMessage());
//    }
//
//    private static ErrorResponse errorResponse(Integer errorCode,
//                                               String message) {
//        return errorResponse(errorCode, message, message);
//    }
//
//    private static ErrorResponse errorResponse(Integer errorCode,
//                                               String message,
//                                               String displayMessage) {
//        return ErrorResponse.builder()
//                .errorCode(errorCode)
//                .message(message)
//                .displayMessage(displayMessage)
//                .build();
//    }
}
