package space.gavinklfong.demo.streamapi.filter;

import space.gavinklfong.demo.streamapi.models.Order;

import java.time.Month;

public class FilterOrdersFebruary implements OrderPredicate{

  @Override
  public boolean test(Order order) {
    return order.getOrderDate().getMonth().equals(Month.FEBRUARY);
  }
}
