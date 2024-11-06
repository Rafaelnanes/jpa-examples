package com.rafael.jpa;

import com.rafael.jpa.model.Product;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class PerformanceQueryTests extends AbstractTestCase {

  private static final int PRODUCT_ID = 1;

  /**
   * The best scenario: Chaining transactions makes hibernate only run select once.
   */
  @Test
  void chainTransactional() {
    final TransactionStatus transaction = transactionManager.getTransaction(TransactionDefinition.withDefaults());

    final Product product = productService.find(PRODUCT_ID);
    productService.decrementAndMerge(product);
    final Product productFromDb = productService.find(PRODUCT_ID);

    transactionManager.commit(transaction);

    assertEquals(productFromDb.getStock(), 2);
    assertEquals(1, QueryCountHolder.getGrandTotal().getSelect()); //just once
    assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
  }

  /**
   * Bad scenario
   */
  @Test
  void unnecessarySelectQueryBecauseWasNotInTransaction() {
    final Product product = productService.find(PRODUCT_ID);
    productService.decrementAndMerge(product);
    final Product productFromDb = productService.find(PRODUCT_ID);

    assertEquals(productFromDb.getStock(), 2);
    assertEquals(3, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(PRODUCT_ID, QueryCountHolder.getGrandTotal().getUpdate());
  }

  /**
   * Stock has not changed because the entity as detached and was no merged or saved
   */
  @Test
  void stockNotChanged() {
    final Product product = productService.find(PRODUCT_ID);
    productService.decrement(product);
    final Product productFromDb = productService.find(PRODUCT_ID);

    assertEquals(productFromDb.getStock(), 3);// not changed
    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(0, QueryCountHolder.getGrandTotal().getUpdate()); //no updates
  }

  /**
   * After finishing the transaction the entity manager triggers
   * flush by a dirty check(Entity manager will verify if there is difference between the classes and then run db update)
   */
  @Test
  void mergeByFlushAutomatically() {
    productService.decrementWithNoMerge(PRODUCT_ID);
    final Product productFromDb = productService.find(PRODUCT_ID);

    assertEquals(productFromDb.getStock(), 2);
    assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
    assertEquals(1, QueryCountHolder.getGrandTotal().getUpdate());
  }

}
