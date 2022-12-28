package com.zenika.users.service;

import com.zenika.users.dto.SimpleResponseDto;
import com.zenika.users.dto.UsersDto;
import com.zenika.users.dto.UsersListDto;

import java.io.InputStream;
import java.util.Map;

public interface UserService {

  SimpleResponseDto uploadUsers(InputStream inputStreamCsvData);

  UsersListDto getUsers(double minSalary, double maxSalary, int offset, int limit, String[] sortBy);

  UsersDto getUser(String id);

  SimpleResponseDto createUser(UsersDto usersDto);

  SimpleResponseDto deleteUser(String id);

  SimpleResponseDto updateUser(String id, UsersDto usersDto);

  SimpleResponseDto updateUser(String id, Map<String, Object> usersDataMap);
}
