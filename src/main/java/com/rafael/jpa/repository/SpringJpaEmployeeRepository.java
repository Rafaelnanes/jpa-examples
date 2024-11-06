package com.rafael.jpa.repository;

import com.rafael.jpa.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringJpaEmployeeRepository extends JpaRepository<Employee, Integer> {

  Employee findByName(String name);

}
