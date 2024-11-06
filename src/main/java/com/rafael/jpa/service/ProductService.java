package com.rafael.jpa.service;

import com.rafael.jpa.model.Product;
import com.rafael.jpa.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  public Product find(int id) {
    return productRepository.find(id);
  }

  public void decrementAndMerge(Product product) {
    product.setStock(product.getStock() - 1);
    productRepository.merge(product);
  }

  public void decrement(Product product) {
    product.setStock(product.getStock() - 1);
  }

  @Transactional
  public void decrementWithNoMerge(int id) {
    final Product product = productRepository.find(id);
    product.setStock(product.getStock() - id);
  }

}
