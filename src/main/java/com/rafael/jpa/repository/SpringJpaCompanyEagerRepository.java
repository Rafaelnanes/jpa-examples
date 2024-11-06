package com.rafael.jpa.repository;

import com.rafael.jpa.model.CompanyEager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringJpaCompanyEagerRepository extends JpaRepository<CompanyEager, Integer> {

  CompanyEager findByName(String name);

  @Query("select c from CompanyEager c inner join fetch c.employees")
  List<CompanyEager> findAllWithEmployees();

}
