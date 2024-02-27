
package space.gavinklfong.demo.streamapi.repos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.streamapi.models.Customer;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class AppCommandRunner implements CommandLineRunner {

    @Autowired
    private CustomerRepo customerRepos;

    @Autowired
    private OrderRepo orderRepos;

    @Autowired
    private ProductRepo productRepos;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        log.info("Customers:");
        customerRepos.findAll()
                .forEach(c -> log.info(c.toString()));

        log.info("Orders:");
        orderRepos.findAll()
                .forEach(o -> log.info(o.toString()));

        log.info("Products:");
        productRepos.findAll()
                .forEach(p -> log.info(p.toString()));

    }
    private List<Product> exercise01() {

        return productRepos.findAll()
                .stream()
                .filter( product -> product.getCategory().equalsIgnoreCase("book"))
                .filter(product -> product.getPrice() > 100)
                .toList();

    }
    private List<Order> exercise02() {

        return orderRepos.findAll()
                .stream()
                .filter(order -> order.getProducts()
                .stream()
                .anyMatch(product -> product.getCategory().equalsIgnoreCase("baby")))
                .toList();


    }

    private List<Product> exercise03() {
        /*Exercise 3 — Obtain a list of product with category = “Toys” and then apply
        10% discount
        */

        return productRepos.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("toys"))
                .peek(product -> product.setPrice((Double) (product.getPrice()*0.9)))
                .toList();

    }

    private List<Product> exercise03_1() {
        /*Exercise 3 — Obtain a list of product with category = “Toys” and then apply
        10% discount
        */

        return productRepos.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("toys"))
                .map(product -> product.withPrice((Double) (product.getPrice()*0.9)))
                .toList();

    }

    private List<Product> exercise04() {
        /* Obtain a list of products ordered by customer of
tier 2 between 01-Feb-2021 and 01-Apr-2021
        */

        return orderRepos.findAll()
                .stream()
                .filter(order -> order.getCustomer().getTier().equals(2))
                .filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)) &&
                                order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1)))
                //.map(Order::getProducts)
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .toList();


    }

    private List<Product> exercise05() {
        /* Exercise 5 — Get the cheapest products of “Books” category
        */

        return productRepos.findAll()
                .stream()
                .filter( product -> product.getCategory().equalsIgnoreCase("Books"))
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .toList();


    }

    private List<Order> exercise06 () {
        //Exercise 6 — Get the 3 most recent placed order

        return orderRepos.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .toList();
    }

    private List<Product> exercise07 () {
        //Exercise 7 — Get a list of orders which were ordered on 15-Mar-2021, log
        //the order records to the console and then return its product list


        return orderRepos.findAll()
                .stream()
                .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .peek(o -> System.out.println(o.toString()))
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .toList();
    }

    private double exercise08 () {
        //Exercise 8 — Calculate total lump sum of all orders placed in Feb 2021


        return orderRepos.findAll()
                .stream()
                .filter(o -> o.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)) &&
                            o.getOrderDate().isAfter(LocalDate.of(2021, 1, 31)))
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

    }

    private double exercise09 () {
        //Exercise 9 — Calculate order average payment placed on 14-Mar-2021


        return orderRepos.findAll()
                .stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 14)))
                .flatMap( p -> p.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .average()
                .getAsDouble();

    }

    private DoubleSummaryStatistics exercise10 () {
        //Exercise 10 — Obtain a collection of statistic figures (i.e. sum, average,
        //max, min, count) for all products of category “Books”

        return productRepos.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
    }

    private HashMap<Long, Integer> exercise11 () {
        //Exercise 11 — Obtain a data map with order id and order’s product count

        return (HashMap<Long, Integer>) orderRepos.findAll()
                .stream()
                .collect(Collectors.toMap(
                        order -> order.getId(),
                        order -> order.getProducts().size()
                ));
    }

    private HashMap<Customer, List<Order>> exercise12 () {
        //Exercise 12 — Produce a data map with order records grouped by customer

        return (HashMap<Customer, List<Order>>) orderRepos.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
    }
//joder
    private HashMap<Order, Double> exercise13 () {
        //Exercise 13 — Produce a data map with order record and product total sum

        return (HashMap<Order, Double>) orderRepos.findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                        Function.identity(),
                        order -> order.getProducts().stream()
                                .mapToDouble(p -> p.getPrice()).sum())
                );



    }

    private HashMap<String, List<String>> exercise14 () {
        //Exercise 14 — Obtain a data map with list of product name by category

        return (HashMap<String, List<String>>) productRepos.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.mapping(product -> product.getName(), Collectors.toList()))
                );
    }

    private HashMap<String, Optional<Product>> exercise15 () {
        //Exercise 15 — Get the most expensive product by category


        return (HashMap<String, Optional<Product>>) productRepos.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ));
    }
}
