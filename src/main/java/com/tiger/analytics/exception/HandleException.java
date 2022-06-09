package com.tiger.analytics.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class HandleException {
  @ExceptionHandler({JsonMappingException.class, MethodArgumentNotValidException.class, InvalidFormatException.class})
  public ResponseEntity<ResponseException> checkInputFormat(Exception exception) {
    String errorReason = "Invalid input data";

    if (exception.getCause() instanceof JsonMappingException) {
      errorReason = ((JsonMappingException) exception.getCause()).getOriginalMessage() != null ? ((JsonMappingException) exception.getCause()).getOriginalMessage() : errorReason;
    }
    if (exception.getCause() instanceof InvalidFormatException) {
      String fieldName = "";
      if (((InvalidFormatException) exception.getCause()).getPath() != null && !CollectionUtils
          .isEmpty(((InvalidFormatException) exception.getCause()).getPath()) && ((InvalidFormatException) exception.getCause()).getPath().get(0) != null) {
        fieldName = ((InvalidFormatException) exception.getCause()).getPath().get(0).getFieldName();
        errorReason = fieldName + " need to adhere to specific format. Provide valid " + fieldName;
      }
    }
    if (exception.getCause() instanceof MethodArgumentNotValidException || exception instanceof MethodArgumentNotValidException) {
      if (!CollectionUtils.isEmpty(((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors()) && ((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors().get(0) != null) {
        String filedName = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors().get(0).getField();
        String message = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        if (StringUtils.hasText(filedName) && StringUtils.hasText(message)) {
          errorReason = filedName + " " + message;
        }
      }
    }
    ResponseException responseException = new ResponseException(new Date(), HttpStatus.BAD_REQUEST.value(), "Invalid Request", errorReason);
    return new ResponseEntity<ResponseException>(responseException, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}