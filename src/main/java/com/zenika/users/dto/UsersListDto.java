package com.zenika.users.dto;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UsersListDto {
  private List<UsersDto> results;
}
