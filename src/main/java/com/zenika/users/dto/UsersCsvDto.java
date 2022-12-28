package com.zenika.users.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class UsersCsvDto {
  @CsvBindByName(column = "id", required = true)
  private String id;

  @CsvBindByName(column = "login", required = true)
  private String login;

  @CsvBindByName(column = "name", required = true)
  private String name;

  @CsvBindByName(column = "salary", required = true)
  @Min(value = 0, message = "Salary cannot be negative")
  private Double salary;

  @CsvBindByName(column = "startDate", required = true)
  @CsvDate(value = "[yyyy-MM-dd][dd-MMM-yy]")
  private LocalDate startDate;
}
