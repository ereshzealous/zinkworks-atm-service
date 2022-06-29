package com.zinkworks.atm.exceptions;

public class DataValidationException extends RuntimeException {
  private String code;

  public DataValidationException(String message, String code) {
    super(message);
    this.code = code;
  }
}
