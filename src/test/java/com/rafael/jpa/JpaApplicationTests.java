package com.rafael.jpa;

import com.rafael.jpa.model.Product;
import com.rafael.jpa.service.ProductService;
import com.rafael.jpa.service.SpringJpaProductRepository;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
class JpaApplicationTests {

  private static final int PRODUCT_ID = 1;

  @Autowired
  private ProductService productService;

  @Autowired
  private SpringJpaProductRepository springJpaProductRepository;

  @BeforeEach
  void each() {
    springJpaProductRepository.save(new Product(PRODUCT_ID, "Bag", 3));
    springJpaProductRepository.save(new Product(2, "Bottle", 7));
    springJpaProductRepository.save(new Product(3, "Pc", 5));
    QueryCountHolder.clear();
  }

  @AfterEach
  void wipeData() {
    final List<Product> products = springJpaProductRepository.findAll();
    springJpaProductRepository.deleteAll(products);
  }

  @Test
  void unnecessarySelectQueryBecauseWasNotInTransaction() {
    final Product product = productService.find(PRODUCT_ID);
    productService.decrementAndMerge(product);
    final Product productFromDb = productService.find(PRODUCT_ID);

    assertEquals(productFromDb.getStock(), 2);
    assertEquals(3, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(PRODUCT_ID, QueryCountHolder.getGrandTotal().getUpdate());
  }

  @Test
  void stockNotChanged() {
    final Product product = productService.find(PRODUCT_ID);
    productService.decrement(product);
    final Product productFromDb = productService.find(PRODUCT_ID);

    assertEquals(productFromDb.getStock(), 3);// not changed
    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(0, QueryCountHolder.getGrandTotal().getUpdate()); //no inserts
  }

  @Test
  void mergeByFlush() {
    productService.decrementWithNoMerge(PRODUCT_ID);
    final Product productFromDb = productService.find(PRODUCT_ID);

    assertEquals(productFromDb.getStock(), 2);
    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
  }

}
