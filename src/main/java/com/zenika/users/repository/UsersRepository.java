package com.zenika.users.repository;

import com.zenika.users.entity.Users;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends PagingAndSortingRepository<Users, String> {

  List<Users> findBySalaryGreaterThanEqualAndSalaryLessThan(double maxSalary, double minSalary, Sort by);

  List<Users> findByLogin(String login);
}
