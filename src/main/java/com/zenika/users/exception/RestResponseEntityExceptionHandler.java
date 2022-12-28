package com.zenika.users.exception;

import com.zenika.users.dto.ResponseMessage;
import com.zenika.users.dto.SimpleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  protected ResponseEntity<SimpleResponseDto> handleRuntimeException(RuntimeException ex) {
    SimpleResponseDto responseDto =
        new SimpleResponseDto(
            ResponseMessage.ERROR_OCCURRED_BAD_INPUT,
            ex.getMessage() + " " + ex.getCause().getMessage());
    log.error("Error occurred", ex);
    return new ResponseEntity<>(
        responseDto, ResponseMessage.ERROR_OCCURRED_BAD_INPUT.getResponseStatus());
  }

  @ExceptionHandler(InvalidUserDataException.class)
  protected ResponseEntity<SimpleResponseDto> handleInvalidUserDataException(
      InvalidUserDataException ex) {
    SimpleResponseDto responseDto =
        new SimpleResponseDto(ResponseMessage.ERROR_OCCURRED_BAD_INPUT, ex.getMessage());
    log.error("Error occurred", ex);
    return new ResponseEntity<>(
        responseDto, ResponseMessage.ERROR_OCCURRED_BAD_INPUT.getResponseStatus());
  }

  @ExceptionHandler(DuplicateEmployeeIdException.class)
  protected ResponseEntity<SimpleResponseDto> handleDuplicateEmployeeIdException(
      DuplicateEmployeeIdException ex) {
    SimpleResponseDto responseDto = new SimpleResponseDto(ResponseMessage.DUPLICATE_EMPLOYEE_ID);
    log.error("Error occurred", ex);
    return new ResponseEntity<>(
        responseDto, ResponseMessage.DUPLICATE_EMPLOYEE_ID.getResponseStatus());
  }

  @ExceptionHandler(DuplicateLoginException.class)
  protected ResponseEntity<SimpleResponseDto> handleDuplicateLoginException(
      DuplicateLoginException ex) {
    SimpleResponseDto responseDto = new SimpleResponseDto(ResponseMessage.DUPLICATE_LOGIN_ID);
    log.error("Error occurred", ex);
    return new ResponseEntity<>(
        responseDto, ResponseMessage.DUPLICATE_LOGIN_ID.getResponseStatus());
  }

  @ExceptionHandler(UserNotFoundException.class)
  protected ResponseEntity<SimpleResponseDto> handleUserNotFoundException(
      UserNotFoundException ex) {
    SimpleResponseDto responseDto = new SimpleResponseDto(ResponseMessage.USER_NOT_FOUND);
    log.error("Error occurred", ex);
    return new ResponseEntity<>(responseDto, ResponseMessage.USER_NOT_FOUND.getResponseStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    log.error("Error occurred", ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    SimpleResponseDto responseDto =
        new SimpleResponseDto(ResponseMessage.INVALID_USER_DATA, errors.toString());
    return new ResponseEntity<>(responseDto, ResponseMessage.INVALID_USER_DATA.getResponseStatus());
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<SimpleResponseDto> handleException(Exception ex) {
    SimpleResponseDto responseDto = new SimpleResponseDto(ResponseMessage.INTERNAL_SERVER_ERROR);
    log.error("Error occurred", ex);
    return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
