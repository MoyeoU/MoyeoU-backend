package com.moyeou.moyeoubackend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

import static com.moyeou.moyeoubackend.common.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = errors(exception);
        ErrorResponse response = new ErrorResponse(INVALID_REQUEST_PARAMS, errors);
        log.warn("[{}|{}] resultCode: {}, resultMessage: {}, resultErrors: {}",
                request.getRequestURI(), request.getMethod(), response.getCode(), response.getMessage(), response.getErrors());
        return new ResponseEntity<>(response, INVALID_REQUEST_PARAMS.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(ENTITY_NOT_FOUND.getCode(), exception.getMessage());
        log.warn("[{}|{}] resultCode: {}, resultMessage: {}",
                request.getRequestURI(), request.getMethod(), response.getCode(), response.getMessage());
        return new ResponseEntity<>(response, ENTITY_NOT_FOUND.getStatus());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(ILLEGAL_STATE.getCode(), exception.getMessage());
        log.warn("[{}|{}] resultCode: {}, resultMessage: {}",
                request.getRequestURI(), request.getMethod(), response.getCode(), response.getMessage());
        return new ResponseEntity<>(response, ILLEGAL_STATE.getStatus());
    }

    @ExceptionHandler(MoyeouException.class)
    public ResponseEntity<ErrorResponse> handleMoyeouException(MoyeouException exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(exception.getCode(), exception.getMessage());
        log.warn("[{}|{}] resultCode: {}, resultMessage: {}",
                request.getRequestURI(), request.getMethod(), response.getCode(), response.getMessage());
        return new ResponseEntity<>(response, exception.getStatus());
    }

    private Map<String, String> errors(MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(this::field, this::message));
    }

    private String field(ObjectError error) {
        return ((FieldError) error).getField();
    }

    private String message(ObjectError error) {
        return error.getDefaultMessage();
    }
}
