package com.zenika.users.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "USERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
  @Id
  private String id;
  @Column(unique = true)
  @NotNull
  private String login;
  @NotNull
  private String name;
  @Min(0)
  private Double salary;
  @NotNull
  private LocalDate startDate;
}
