package com.rafael.jpa;

import com.rafael.jpa.model.CompanyLazy;
import com.rafael.jpa.model.Employee;
import com.rafael.jpa.model.Product;
import com.rafael.jpa.repository.SpringJpaCompanyEagerRepository;
import com.rafael.jpa.repository.SpringJpaCompanyLazyRepository;
import com.rafael.jpa.repository.SpringJpaEmployeeRepository;
import com.rafael.jpa.repository.SpringJpaProductRepository;
import com.rafael.jpa.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.List;

@Slf4j
public abstract class AbstractTestCase {

  @Autowired
  protected SpringJpaCompanyEagerRepository springJpaCompanyEagerRepository;

  @Autowired
  protected SpringJpaCompanyLazyRepository springJpaCompanyLazyRepository;

  @Autowired
  protected SpringJpaEmployeeRepository springJpaEmployeeRepository;

  @Autowired
  protected PlatformTransactionManager transactionManager;

  @Autowired
  protected ProductService productService;

  @Autowired
  protected SpringJpaProductRepository springJpaProductRepository;

  @Autowired
  protected EntityManager entityManager;

  private TransactionStatus transaction;

  @BeforeEach
  void beforeEach() {
    // Products
    springJpaProductRepository.save(new Product(1, "Bag", 3));
    springJpaProductRepository.save(new Product(2, "Bottle", 7));
    springJpaProductRepository.save(new Product(3, "Pc", 5));

    // Companies
    final CompanyLazy microsoft = springJpaCompanyLazyRepository.save(new CompanyLazy(1, "Microsoft", null));
    final CompanyLazy apple = springJpaCompanyLazyRepository.save(new CompanyLazy(2, "Apple", null));
    final CompanyLazy hp = springJpaCompanyLazyRepository.save(new CompanyLazy(3, "HP", null));

    // Employees
    springJpaEmployeeRepository.save(new Employee(1, "Employee1", microsoft));
    springJpaEmployeeRepository.save(new Employee(2, "Employee2", microsoft));
    springJpaEmployeeRepository.save(new Employee(3, "Employee3", apple));
    springJpaEmployeeRepository.save(new Employee(4, "Employee4", apple));
    springJpaEmployeeRepository.save(new Employee(5, "Employee5", hp));
    QueryCountHolder.clear();
    log.info("### BEGIN TEST");
  }

  @AfterEach
  void wipeData() {
    log.info("### END TEST");
    springJpaEmployeeRepository.deleteAll(springJpaEmployeeRepository.findAll());
    springJpaCompanyEagerRepository.deleteAll(springJpaCompanyEagerRepository.findAll());
    springJpaProductRepository.deleteAll(springJpaProductRepository.findAll());
  }

  protected CompanyLazy queryCompanyLazyById(int value) {
    return entityManager.createQuery("SELECT c FROM CompanyLazy c WHERE c.id = :id", CompanyLazy.class)
                        .setParameter("id", value)
                        .getSingleResult();
  }

  protected List<CompanyLazy> queryAllCompanyLazy() {
    return entityManager.createQuery("FROM CompanyLazy", CompanyLazy.class)
                        .getResultList();
  }


  protected List<CompanyLazy> queryAllCompanyLazyByIdUsingJoinFetch() {
    return entityManager.createQuery("FROM CompanyLazy c JOIN FETCH c.employees", CompanyLazy.class)
                        .getResultList();
  }

  protected List<CompanyLazy> queryAllCompanyLazyByIdUsingCriteria() {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<CompanyLazy> criteriaQuery = criteriaBuilder.createQuery(CompanyLazy.class);
    final Root<CompanyLazy> root = criteriaQuery.from(CompanyLazy.class);
    root.fetch("employees");
    final TypedQuery<CompanyLazy> typedQuery = entityManager.createQuery(criteriaQuery);
    return typedQuery.getResultList();
  }

  protected void createTransaction() {
    this.transaction = transactionManager.getTransaction(TransactionDefinition.withDefaults());
  }

  protected void commit() {
    transactionManager.commit(transaction);
  }

}
