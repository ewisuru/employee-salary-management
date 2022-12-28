package com.zenika.users.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ResponseMessage {

  USERS_CREATED("Users successfully created", HttpStatus.CREATED),
  USERS_UPDATED("Users successfully updated", HttpStatus.OK),
  ERROR_OCCURRED_BAD_INPUT("Error occurred due to bad input", HttpStatus.BAD_REQUEST),
  FILE_READ_ERROR("Error occurred during reading the incoming file", HttpStatus.BAD_REQUEST),
  DUPLICATE_EMPLOYEE_ID("Employee ID already exists", HttpStatus.BAD_REQUEST),
  DUPLICATE_LOGIN_ID("Employee login not unique", HttpStatus.BAD_REQUEST),
  USER_CREATED("Successfully created", HttpStatus.CREATED),
  USER_DELETED("Employee deleted", HttpStatus.OK),
  USER_NOT_FOUND("No such employee", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_UPDATED("Successfully updated", HttpStatus.OK),
  INVALID_USER_DATA("One or more employee fields are missing or have invalid values", HttpStatus.BAD_REQUEST);

  @Getter(onMethod_ = @JsonValue)
  private String message;
  private HttpStatus responseStatus;
}
