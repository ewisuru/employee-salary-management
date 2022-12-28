package com.zenika.users.mapper;

import com.zenika.users.dto.UsersCsvDto;
import com.zenika.users.entity.Users;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsersCsvDtoToUsersMapper {

  List<Users> mapToUsers(List<UsersCsvDto> usersCsvDto);
}
