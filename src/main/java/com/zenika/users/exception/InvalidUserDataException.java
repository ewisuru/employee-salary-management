package com.zenika.users.exception;

public class InvalidUserDataException extends RuntimeException {
  public InvalidUserDataException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public InvalidUserDataException(String message) {
    super(message);
  }
}
