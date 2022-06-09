package com.tiger.analytics.exception;

import org.springframework.http.HttpStatus;

public class CaseStudyExceptionHandler extends Exception {

  private HttpStatus exceptionCode;
  private String exceptionError;
  private String exceptionReason;

  public CaseStudyExceptionHandler(HttpStatus exceptionCode, String exceptionError, String exceptionReason) {
    this.exceptionCode = exceptionCode;
    this.exceptionError = exceptionError;
    this.exceptionReason = exceptionReason;
  }

  public HttpStatus getExceptionCode() {
    return exceptionCode;
  }

  public String getExceptionError() {
    return exceptionError;
  }

  public String getExceptionReason() {
    return exceptionReason;
  }
}
