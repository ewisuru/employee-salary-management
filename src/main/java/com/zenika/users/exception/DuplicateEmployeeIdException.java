package com.zenika.users.exception;

public class DuplicateEmployeeIdException extends RuntimeException {
  public DuplicateEmployeeIdException(String message) {
    super(message);
  }
}
