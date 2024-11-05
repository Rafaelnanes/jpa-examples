package com.rafael.jpa.service;

import com.rafael.jpa.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProductRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public Product find(int id) {
    return entityManager.find(Product.class, id);
  }

  @Transactional
  public void merge(Product product) {
    entityManager.merge(product);
  }

  @Transactional
  public void persist(Product product) {
    entityManager.persist(product);
  }
}
