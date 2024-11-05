package com.rafael.jpa.service;

import com.rafael.jpa.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringJpaProductRepository extends JpaRepository<Product, Integer> {
}
