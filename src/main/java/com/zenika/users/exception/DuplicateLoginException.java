package com.zenika.users.exception;

public class DuplicateLoginException extends RuntimeException {
  public DuplicateLoginException(String message) {
    super(message);
  }
}
