package com.rafael.jpa;

import com.rafael.jpa.model.CompanyLazy;
import com.rafael.jpa.model.Employee;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FlushModeTests extends AbstractTestCase {

  @Test
  void flushModeDirty() {
    createTransaction();

    final CompanyLazy company = springJpaCompanyLazyRepository.findById(1).get();

    springJpaEmployeeRepository.save(new Employee("AnyEmployee", company));
    springJpaEmployeeRepository.findAll();
    springJpaEmployeeRepository.save(new Employee("AnyEmployee2", company));

    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(1, QueryCountHolder.getGrandTotal().getInsert());

    commit();

    assertEquals(2, QueryCountHolder.getGrandTotal().getInsert());
  }

  @Test
  void flushModeDirtyButFindAllForADifferentEntity() {
    createTransaction();

    final CompanyLazy company = springJpaCompanyLazyRepository.findById(1).get();

    springJpaEmployeeRepository.save(new Employee("AnyEmployee", company));
    entityManager.createQuery("FROM DifferentProduct").getResultList();
    springJpaEmployeeRepository.save(new Employee("AnyEmployee2", company));

    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(0, QueryCountHolder.getGrandTotal().getInsert());

    commit();

    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
  }

  @Test
  void flushModeManually() {
    createTransaction();

    final CompanyLazy company = springJpaCompanyLazyRepository.findById(1).get();

    springJpaEmployeeRepository.save(new Employee("AnyEmployee", company));
    springJpaEmployeeRepository.findAll();
    springJpaEmployeeRepository.save(new Employee("AnyEmployee2", company));
    entityManager.flush();

    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(2, QueryCountHolder.getGrandTotal().getInsert());

    commit();

  }

}
