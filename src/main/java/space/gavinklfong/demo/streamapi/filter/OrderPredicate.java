package space.gavinklfong.demo.streamapi.filter;

import space.gavinklfong.demo.streamapi.models.Order;

@FunctionalInterface
public interface OrderPredicate {

  boolean test(Order order);

  default boolean myTest(Order order) {
    return order.getId() == 1;
  }
}
