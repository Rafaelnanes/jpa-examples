package com.rafael.jpa.repository;

import com.rafael.jpa.model.CompanyLazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringJpaCompanyLazyRepository extends JpaRepository<CompanyLazy, Integer> {

  @Query("SELECT c FROM CompanyLazy c JOIN FETCH c.employees")
  List<CompanyLazy> findAllWithEmployees();

  @Query("SELECT c FROM CompanyLazy c JOIN FETCH c.employees WHERE c.id =:id ")
  CompanyLazy findAllWithEmployeesByCompanyId(int id);

}
