package com.rafael.jpa;

import com.rafael.jpa.model.CompanyEager;
import com.rafael.jpa.model.CompanyLazy;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OneToManyTests extends AbstractTestCase {

  @Test
  void findAll_lazyOverheadQuery() {
    createTransaction();

    AtomicInteger atomicInteger = new AtomicInteger();
    final List<CompanyLazy> companies = queryAllCompanyLazy();
    companies.forEach(x -> atomicInteger.set(atomicInteger.get() + x.getEmployees().size()));

    commit();

    assertEquals(5, atomicInteger.get());
    assertEquals(4, QueryCountHolder.getGrandTotal().getSelect());
  }

  @Test
  void findAll_lazyUsingEntityManagerJoinFetch() {
    createTransaction();

    AtomicInteger atomicInteger = new AtomicInteger();
    final List<CompanyLazy> companies = queryAllCompanyLazyByIdUsingJoinFetch();
    companies.forEach(x -> atomicInteger.set(atomicInteger.get() + x.getEmployees().size()));

    commit();

    assertEquals(5, atomicInteger.get());
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
  }


  @Test
  void findAll_lazyUsingEntityManagerCriteriaBuilder() {
    createTransaction();

    AtomicInteger atomicInteger = new AtomicInteger();
    final List<CompanyLazy> companies = queryAllCompanyLazyByIdUsingCriteria();
    companies.forEach(x -> atomicInteger.set(atomicInteger.get() + x.getEmployees().size()));

    commit();

    assertEquals(5, atomicInteger.get());
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
  }

  @Test
  void findAll_LazyWithOneQueryUsingSpringJpa() {
    AtomicInteger atomicInteger = new AtomicInteger();

    createTransaction();

    final List<CompanyLazy> companies = springJpaCompanyLazyRepository.findAllWithEmployees();
    companies.forEach(x -> atomicInteger.set(atomicInteger.get() + x.getEmployees().size()));

    commit();

    assertEquals(5, atomicInteger.get());
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
  }

  @Test
  void findOne_OneQuery() {
    createTransaction();

    CompanyLazy company = springJpaCompanyLazyRepository.findAllWithEmployeesByCompanyId(1);
    final int size = company.getEmployees().size();

    commit();

    assertEquals(2, size);
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
  }

  // ## EAGER
  @Test
  void eagerWithOneQueryUsingSpringJpa() {
    AtomicInteger atomicInteger = new AtomicInteger();

    createTransaction();
    final List<CompanyEager> companies = springJpaCompanyEagerRepository.findAllWithEmployees();
    companies.forEach(x -> atomicInteger.set(atomicInteger.get() + x.getEmployees().size()));

    commit();

    assertEquals(5, atomicInteger.get());
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
  }

}
