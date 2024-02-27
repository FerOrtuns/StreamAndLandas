package space.gavinklfong.demo.streamapi.filter;

import space.gavinklfong.demo.streamapi.models.Order;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CustomFilter<T> {


  public List<Order> filterHardcoded(List<Order> list) {
    List<Order> auxList = new ArrayList<>();

    for(Order order : list) {
      if(order.getOrderDate().getMonth().equals(Month.FEBRUARY))
        auxList.add(order);
    }

    return auxList;
  }

  public List<Order> filterByMonth(List<Order> list, Month month) {
    List<Order> auxList = new ArrayList<>();

    for(Order order : list) {
      if(order.getOrderDate().getMonth().equals(month))
        auxList.add(order);
    }

    return auxList;
  }

  public List<Order> filterByOrderPredicate(List<Order> orders, OrderPredicate orderPredicate) {
    List<Order> auxList = new ArrayList<>();

    for(Order order : orders) {
      if(orderPredicate.test(order))
        auxList.add(order);
    }

    return auxList;
  }

  public List<T> filterByMyPredicate(List<T> items, MyPredicate<T> myPredicate) {
    List<T> auxList = new ArrayList<>();

    for(T item : items) {
      if(myPredicate.test(item))
        auxList.add(item);
    }

    return auxList;
  }

  public List<T> filterJDKPredicate(List<T> list, Predicate<T> predicate) {
    List<T> auxList = new ArrayList<>();

    for(T item : list) {
      if(predicate.test(item))
        auxList.add(item);
    }

    return auxList;
  }


}
