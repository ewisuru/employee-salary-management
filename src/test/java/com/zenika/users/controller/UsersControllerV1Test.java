package com.zenika.users.controller;

import com.zenika.users.dto.ResponseMessage;
import com.zenika.users.dto.SimpleResponseDto;
import com.zenika.users.dto.UsersDto;
import com.zenika.users.dto.UsersListDto;
import com.zenika.users.service.UserService;
import com.zenika.users.testutils.TestFileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UsersControllerV1.class)
public class UsersControllerV1Test {

  @Autowired private MockMvc mockMvc;
  @MockBean private UserService userService;

  @Test
  @DisplayName("When file contains all new users, upload should return 201")
  void uploadFileWithAllNewDataShouldReturn201() throws Exception {
    MockMultipartFile file = givenValidMultipartFile();
    givenUserServiceReturnsUsersCreated();
    mockMvc
        .perform(multipart("/v1/users/upload").file(file))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(ResponseMessage.USERS_CREATED.getMessage()))
        .andReturn();
  }

  @Test
  @DisplayName("When file contains some existing users, upload should return 202")
  void uploadFileWithAllNewDataShouldReturn200() throws Exception {
    MockMultipartFile file = givenValidMultipartFile();
    givenUserServiceReturnsUsersUpdated();
    mockMvc
        .perform(multipart("/v1/users/upload").file(file))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(ResponseMessage.USERS_UPDATED.getMessage()))
        .andReturn();
  }

  @Test
  @DisplayName("When file input stream read fails, upload should return 400")
  void uploadFileWithAllNewDataShouldReturn400() throws Exception {
    MockMultipartFile file = givenInvalidMultipartFile();
    mockMvc
        .perform(multipart("/v1/users/upload").file(file))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ResponseMessage.FILE_READ_ERROR.getMessage()))
        .andReturn();
  }

  @Test
  @DisplayName("When fetch users is called without any query parameters, request should be served")
  void fetchUsersWithoutAnyQueryParametersShouldReturn200() throws Exception {
    givenUserServiceReturnUserResult();
    mockMvc
        .perform(get("/v1/users"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results").exists())
        .andReturn();
  }

  private void givenUserServiceReturnUserResult() {
    UsersDto usersDto = new UsersDto("001", "loginId", "name", 1000D, LocalDate.now());
    when(userService.getUsers(anyDouble(), anyDouble(), anyInt(), anyInt(), any()))
        .thenReturn(new UsersListDto(List.of(usersDto)));
  }

  private void givenUserServiceReturnsUsersCreated() {
    when(userService.uploadUsers(any()))
        .thenReturn(new SimpleResponseDto(ResponseMessage.USERS_CREATED));
  }

  private void givenUserServiceReturnsUsersUpdated() {
    when(userService.uploadUsers(any()))
        .thenReturn(new SimpleResponseDto(ResponseMessage.USERS_UPDATED));
  }

  private MockMultipartFile givenValidMultipartFile() throws IOException {
    return new MockMultipartFile(
        "file", TestFileReader.readFile(TestFileReader.VALID_CSV_DATA_SOURCE));
  }

  private MockMultipartFile givenInvalidMultipartFile() throws IOException {
    return new MockMultipartFile(
        "file", TestFileReader.readFile(TestFileReader.VALID_CSV_DATA_SOURCE)) {
      @Override
      public InputStream getInputStream() throws IOException {
        throw new IOException("exception");
      }
    };
  }
}
