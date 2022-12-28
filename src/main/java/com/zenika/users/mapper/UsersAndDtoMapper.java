package com.zenika.users.mapper;

import com.zenika.users.dto.UsersDto;
import com.zenika.users.dto.UsersListDto;
import com.zenika.users.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface UsersAndDtoMapper {

  default UsersListDto mapToUsersListDto(List<Users> users) {
    return mapToUsersListDto(0, users);
  }

  @Mapping(source = "users", target = "results")
  UsersListDto mapToUsersListDto(Integer dummy, List<Users> users);

  UsersDto mapToUsersDto(Users users);

  Users mapToUsers(UsersDto usersDto);

  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget Users existingUser, UsersDto usersDto);

  default void update(@MappingTarget Users existingUser, Map<String, Object> usersDataMap) {
    if (usersDataMap.containsKey("login")) {
      existingUser.setLogin((usersDataMap.get("login").toString()));
    }
    if (usersDataMap.containsKey("name")) {
      existingUser.setName(usersDataMap.get("name").toString());
    }
    if (usersDataMap.containsKey("salary")) {
      existingUser.setSalary(Double.parseDouble(usersDataMap.get("salary").toString()));
    }
    if (usersDataMap.containsKey("startDate")) {
      existingUser.setStartDate(
          LocalDate.parse(
              usersDataMap.get("startDate").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
  }
}
