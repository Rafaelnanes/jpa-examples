package com.rafael.jpa;

import com.rafael.jpa.model.CompanyEager;
import com.rafael.jpa.model.CompanyLazy;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LazyInitializationTests extends AbstractTestCase {

  @Test
  void entityManaged() {
    createTransaction();

    final CompanyEager companyEager = springJpaCompanyEagerRepository.findById(1).get();
    final int employeeSize = companyEager.getEmployees().size();

    commit();

    assertEquals(2, employeeSize);
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());
  }

  /**
   * In lazy initialization has two more selects
   */
  @Test
  void getEmployeesStatementWithinTransaction() {
    createTransaction();

    final CompanyLazy companyLazy = queryCompanyLazyById(1);
    final int employeeSize = companyLazy.getEmployees().size();

    commit();

    assertEquals(2, employeeSize);
    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
  }

  /**
   * Triggers Hibernate.initialize within the transaction
   * In lazy initialization has two more selects
   */
  @Test
  void forceHibernateInitialization() {
    createTransaction();

    final CompanyLazy companyLazy = queryCompanyLazyById(1);
    Hibernate.initialize(companyLazy.getEmployees());

    commit();

    final int employeeSize = companyLazy.getEmployees().size();

    assertEquals(2, employeeSize);
    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
  }


  /**
   * It tries to initialize the object outside transaction
   */
  @Test
  void hibernateInitializationException() {
    createTransaction();

    final CompanyLazy companyLazy = queryCompanyLazyById(1);

    commit();

    Assertions.assertThrows(LazyInitializationException.class, () -> {
      Hibernate.initialize(companyLazy.getEmployees());
    });

  }

}
