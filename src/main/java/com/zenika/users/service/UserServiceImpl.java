package com.zenika.users.service;

import com.zenika.users.dto.*;
import com.zenika.users.entity.Users;
import com.zenika.users.exception.DuplicateEmployeeIdException;
import com.zenika.users.exception.DuplicateLoginException;
import com.zenika.users.exception.InvalidUserDataException;
import com.zenika.users.exception.UserNotFoundException;
import com.zenika.users.mapper.UsersAndDtoMapper;
import com.zenika.users.mapper.UsersCsvDtoToUsersMapper;
import com.zenika.users.repository.UsersRepository;
import com.zenika.users.utils.CsvToBeanConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private UsersRepository usersRepository;
  private UsersCsvDtoToUsersMapper usersCsvDtoToUsersMapper;
  private UsersAndDtoMapper usersAndDtoMapper;
  private static List<Users> users = new ArrayList<>();

  @Override
  public SimpleResponseDto uploadUsers(InputStream inputStreamCsvData) {
    List<UsersCsvDto> usersCsvDtoList =
        CsvToBeanConverter.convertCsvToBean(inputStreamCsvData, UsersCsvDto.class);
    List<Users> usersList = usersCsvDtoToUsersMapper.mapToUsers(usersCsvDtoList);
    users.addAll(usersList);
    validateUsersList(usersList);
    Iterable<Users> existingUsers = getExistingUsers(usersList);
    Iterable<Users> savedUsers = saveUsers(usersList);
    boolean newUsersCreated = IterableUtils.size(existingUsers) != IterableUtils.size(savedUsers);
    log.info(
        "Uploading completed with creating and updating {} users", IterableUtils.size(savedUsers));
    return new SimpleResponseDto(
        newUsersCreated ? ResponseMessage.USERS_CREATED : ResponseMessage.USERS_UPDATED);
  }

  @Override
  public UsersListDto getUsers(
      double minSalary, double maxSalary, int offset, int limit, String[] sortByInput) {

    List<Sort.Order> orders = new ArrayList<>();
    String[] sortBy = processSortByInput(sortByInput);
    for (String sort : sortBy) {
      String[] fieldAndDirection = sort.split(",");
      String fieldName = validatedUsersField(fieldAndDirection[0]);
      Sort.Direction direction = validatedSortDirection(fieldAndDirection[1]);
      orders.add(new Sort.Order(direction, fieldName));
    }
    List<Users> users =
        usersRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(
            minSalary, maxSalary, Sort.by(orders));
    List<Users> paginatedList =
        users.stream()
            .skip(offset)
            .limit(limit > 0 ? limit : users.size())
            .collect(Collectors.toList());
    return usersAndDtoMapper.mapToUsersListDto(paginatedList);
  }

  @Override
  public UsersDto getUser(String id) {
    Users users = getExistingUserThrowing(id);
    return usersAndDtoMapper.mapToUsersDto(users);
  }

  @Override
  public SimpleResponseDto createUser(UsersDto usersDto) {
    validateIdAndLoginExist(usersDto.getId(), usersDto.getLogin());
    Users users = usersAndDtoMapper.mapToUsers(usersDto);
    saveUser(users);
    return new SimpleResponseDto(ResponseMessage.USER_CREATED);
  }

  @Override
  public SimpleResponseDto deleteUser(String id) {
    getExistingUserThrowing(id);
    usersRepository.deleteById(id);
    return new SimpleResponseDto(ResponseMessage.USER_DELETED);
  }

  @Override
  public SimpleResponseDto updateUser(String id, UsersDto usersDto) {
    Users existingUser = getExistingUserThrowing(id);
    if (!StringUtils.equals(existingUser.getLogin(), usersDto.getLogin())) {
      validateLoginIdAlreadyUsedByOthers(id, usersDto.getLogin());
    }
    usersAndDtoMapper.update(existingUser, usersDto);
    usersRepository.save(existingUser);
    return new SimpleResponseDto(ResponseMessage.USER_UPDATED);
  }

  @Override
  public SimpleResponseDto updateUser(String id, Map<String, Object> usersDataMap) {
    Users existingUser = getExistingUserThrowing(id);
    String originalLoginId = existingUser.getLogin();
    usersAndDtoMapper.update(existingUser, usersDataMap);
    String newLoginId = existingUser.getLogin();
    if (!StringUtils.equals(originalLoginId, newLoginId)) {
      validateLoginIdAlreadyUsedByOthers(id, newLoginId);
    }
    usersRepository.save(existingUser);
    return new SimpleResponseDto(ResponseMessage.USER_UPDATED);
  }

  private void validateIdAndLoginExist(String id, String login) {
    Optional<Users> userById = getExistingUser(id);
    if (userById.isPresent()) {
      log.info("id: {} already exist in DB", id);
      throw new DuplicateEmployeeIdException("Employee id already in use: " + id);
    }
    List<Users> usersByLogin = usersRepository.findByLogin(login);
    if (CollectionUtils.isNotEmpty(usersByLogin)) {
      log.info("login: {} already exist in DB", login);
      throw new DuplicateLoginException("Login id already in use: " + login);
    }
  }

  private String[] processSortByInput(String[] sortByInput) {
    if (sortByInput.length == 2 && !sortByInput[0].contains(",")) {
      sortByInput = new String[] {sortByInput[0] + "," + sortByInput[1]};
    }
    for (String sort : sortByInput) {
      if (!sort.contains(",")) {
        throw new InvalidUserDataException(
            "sortBy must follow format fieldName,direction. Ex: salary:desc but found: " + sort);
      }
    }
    return sortByInput;
  }

  private Sort.Direction validatedSortDirection(String direction) {
    try {
      return Sort.Direction.fromString(direction);
    } catch (Exception ex) {
      log.info("Invalid sort direction provided", ex);
      throw new InvalidUserDataException(ex.getMessage());
    }
  }

  private String validatedUsersField(String fieldName) {
    if (List.of("id", "login", "name", "salary").contains(fieldName.toLowerCase())) {
      return fieldName;
    } else {
      log.info("Invalid sort field name {} provided for sorting users", fieldName);
      throw new InvalidUserDataException(
          "sort field name provided " + fieldName + " is not supported");
    }
  }

  private void validateUsersList(List<Users> usersList) {
    log.info("Validating users");
    validateForDuplicateIds(usersList);
  }

  private void validateForDuplicateIds(List<Users> usersList) {
    log.info("Validating for duplicate IDs");
    Set<String> uniqueIds = new HashSet<>();
    String duplicateIds =
        usersList.stream()
            .map(Users::getId)
            .filter(id -> !uniqueIds.add(id))
            .collect(Collectors.joining(", "));
    if (StringUtils.isNoneBlank(duplicateIds)) {
      log.info("Found duplicate IDs in the users list: {}", duplicateIds);
      throw new InvalidUserDataException("Found duplicate id values: " + duplicateIds);
    }
  }

  private void validateLoginIdAlreadyUsedByOthers(String currentUserId, String login) {
    usersRepository.findByLogin(login).stream()
        .filter(user -> !user.getId().equals(currentUserId))
        .findAny()
        .ifPresent(
            (user) -> {
              throw new DuplicateLoginException("Login ID already used by user: " + user.getId());
            });
  }

  private Optional<Users> getExistingUser(String id) {
    return usersRepository.findById(id);
  }

  private Users getExistingUserThrowing(String id) {
    return getExistingUser(id)
        .orElseThrow(
            () -> {
              log.info("Could not find a user for ID: {}", id);
              return new UserNotFoundException("No user found for id: " + id);
            });
  }

  private Iterable<Users> getExistingUsers(List<Users> usersList) {
    List<String> userIdList = usersList.stream().map(Users::getId).collect(Collectors.toList());
    return usersRepository.findAllById(userIdList);
  }

  private void saveUser(Users users) {
    log.info("Saving user in DB");
    try {
      usersRepository.save(users);
    } catch (ConstraintViolationException | DataIntegrityViolationException ex) {
      log.error("Error occurred while saving user to DB", ex);
      throw new InvalidUserDataException("Error caused by invalid input data: " + ex.getMessage());
    } catch (Exception ex) {
      log.error("Unhandled exception during data save to DB: ", ex);
      throw new RuntimeException(ex);
    }
  }

  private Iterable<Users> saveUsers(List<Users> users) {
    log.info("Saving users in DB");
    try {
      return usersRepository.saveAll(users);
    } catch (ConstraintViolationException | DataIntegrityViolationException ex) {
      log.error("Error occurred while saving users to DB", ex);
      throw new InvalidUserDataException("Error caused by invalid input data: " + ex.getMessage());
    } catch (Exception ex) {
      log.error("Unhandled exception during data save to DB: ", ex);
      throw new RuntimeException(ex);
    }
  }
}
