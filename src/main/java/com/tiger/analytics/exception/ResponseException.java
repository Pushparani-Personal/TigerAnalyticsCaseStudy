package com.tiger.analytics.exception;

import java.util.Date;

public class ResponseException {

  private Date timestamp;
  private int status;
  private String error;
  private String message;

  public ResponseException(Date timestamp, int status, String error, String message) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }
}
