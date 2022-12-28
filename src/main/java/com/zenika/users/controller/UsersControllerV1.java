package com.zenika.users.controller;

import com.zenika.users.dto.ResponseMessage;
import com.zenika.users.dto.SimpleResponseDto;
import com.zenika.users.dto.UsersDto;
import com.zenika.users.dto.UsersListDto;
import com.zenika.users.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@Slf4j
@AllArgsConstructor
public class UsersControllerV1 {

  private UserService userService;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadUsers(@RequestParam("file") MultipartFile file) {
    log.info("User file upload request received");
    try {
      SimpleResponseDto simpleResponseDto = userService.uploadUsers(file.getInputStream());
      return new ResponseEntity<>(
          simpleResponseDto, simpleResponseDto.getMessage().getResponseStatus());
    } catch (IOException ex) {
      log.error("Error occurred during reading the incoming file", ex);
      return new ResponseEntity<>(
          new SimpleResponseDto(ResponseMessage.FILE_READ_ERROR, ex.getMessage()),
          ResponseMessage.FILE_READ_ERROR.getResponseStatus());
    }
  }

  @GetMapping
  public ResponseEntity<?> fetchUsers(
      @RequestParam(required = false, defaultValue = "0") double minSalary,
      @RequestParam(required = false, defaultValue = "4000") double maxSalary,
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "0") int limit,
      @RequestParam(required = false, defaultValue = "id,asc") String[] sortBy) {
    log.info("Received fetch users request");
    UsersListDto usersListDto = userService.getUsers(minSalary, maxSalary, offset, limit, sortBy);
    return ResponseEntity.ok(usersListDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUser(@PathVariable String id) {
    log.info("Received get user request for ID: {}", id);
    UsersDto usersDto = userService.getUser(id);
    return ResponseEntity.ok(usersDto);
  }

  @PostMapping
  public ResponseEntity<?> createUser(@RequestBody @Valid UsersDto usersDto) {
    log.info("Received create user request");
    SimpleResponseDto simpleResponseDto = userService.createUser(usersDto);
    return new ResponseEntity<>(
        simpleResponseDto, simpleResponseDto.getMessage().getResponseStatus());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable String id) {
    log.info("Received delete request with ID: {}", id);
    SimpleResponseDto simpleResponseDto = userService.deleteUser(id);
    return new ResponseEntity<>(
        simpleResponseDto, simpleResponseDto.getMessage().getResponseStatus());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateUser(
      @PathVariable String id, @RequestBody @Valid UsersDto usersDto) {
    log.info("Received update request with ID: {}", id);
    SimpleResponseDto simpleResponseDto = userService.updateUser(id, usersDto);
    return new ResponseEntity<>(
        simpleResponseDto, simpleResponseDto.getMessage().getResponseStatus());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> patchUser(
      @PathVariable String id, @RequestBody Map<String, Object> usersDataMap) {
    log.info("Received patch request with ID: {}", id);
    SimpleResponseDto simpleResponseDto = userService.updateUser(id, usersDataMap);
    return new ResponseEntity<>(
        simpleResponseDto, simpleResponseDto.getMessage().getResponseStatus());
  }
}
