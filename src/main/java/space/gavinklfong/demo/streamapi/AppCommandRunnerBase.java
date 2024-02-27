package space.gavinklfong.demo.streamapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.streamapi.filter.CustomFilter;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class AppCommandRunnerBase implements CommandLineRunner {

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

		/*
		var books = productRepos.findAll()
				.stream()
				.filter(product -> product.getCategory().equals("Books"))
				.filter(product -> product.getPrice() > 800)
				.toList();

		books.forEach(b -> System.out.println(b));

		var list = orderRepos.findAll()
				.stream()
				.filter(order -> order.getProducts()
						.stream()
						.anyMatch(product -> product.getCategory().equals("Baby"))
					   )
				.toList();

		var stats = productRepos.findAll()
				.stream()
				.filter(product -> product.getCategory().equals("Toys"))
				.mapToDouble(product -> product.getPrice())
				.summaryStatistics();

		System.out.println("El precio maximo de los productos es: "+stats);

		var productsBefore = productRepos.findAll();

		var productsAfter = productsBefore
				.stream()
				.filter(product -> product.getCategory().equals("Toys"))
				.peek(product -> product.setPrice(product.getPrice()*0.9))
				//.map(product -> product.withPrice(product.getPrice()*0.9))
				.toList();

		productsAfter.forEach(product -> System.out.println(product));

		 */
		//exercise04();
		//exercise05();
		//exercise06();
		//exercise07();
		//exercise08();

		//exercise09(LocalDate.parse("2021-02-10"));
		implementFilter();
	}

	private void exercise04() {

		var result = orderRepos.findAll().stream()
						.filter(order -> order.getCustomer().getTier() == 2)
						.filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)))
						.filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1)))
						.flatMap(order -> order.getProducts().stream())
						.distinct()
						.toList();


		result.forEach(product -> System.out.println(product));


		var resultSum = orderRepos.findAll().stream()
						.filter(order -> order.getCustomer().getTier() == 2)
						.filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)))
						.filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1)))
						.flatMap(order -> order.getProducts().stream())
						.mapToDouble(product -> product.getPrice())
						.sum();


		System.out.println("Con Stream: " +  resultSum);

		Double total = 0.0;
		List<Order> orders = orderRepos.findAll();
		for (Order order : orders) {
			//	.filter(order -> order.getCustomer().getTier() == 2)
			//	.filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)))
			//	.filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1)))
			if(order.getCustomer().getTier() == 2 &&
							order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)) &&
							order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1))){

				//.flatMap(order -> order.getProducts().stream())
				//	.mapToDouble(product -> product.getPrice())
				//	.sum();
				for(Product product : order.getProducts()) {
					total += product.getPrice();
				}
			}
		}

		System.out.println("Con programación imperativa: " +  total);

	}

	private void exercise05(){
		/*
			One of the effective ways to obtain the product with the lowest price is to sort
			the product list by price in an ascending order and get the first element.
			Java Stream API provides sorted() operation for stream data sorting based on specific field attributes.
			In order to obtain the first element in the stream, you can use the terminal operation findFirst().
			The operation returns the first data element wrapped by Optional as it is possible that the output stream can be empty.
			This solution can only return a single product record with the lowest price.
			If there are multiple product records with the same lowest price, the solution should be modified
			such that it looks for the lowest price amount first and then filters product records by the price amount
			so as to get a list of products with the same lowest price.
		 */
		var cheapestBook = productRepos.findAll().stream()
						.filter(product -> product.getCategory().equalsIgnoreCase("Books"))
						//.min(comparingDouble(Product::getPrice))
						.min((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));

		System.out.println(cheapestBook);
	}

	private void exercise06() {
		/*
			Exercise 6 — Get the 3 most recent placed order
			Similar to previous exercise, the obvious solution is to sort the order records by
			order date field. The tricky part is that the sorting this time should be in descending order such that you can obtain
			the order records with the most recent order date. It can be achieved simply by calling Comparator.reversed().
		 */
		List<Order> salida = orderRepos.findAll()
						.stream()
						.sorted(comparing(Order::getOrderDate).reversed())
						//.sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
						.limit(3)
						.toList();
		salida.forEach(System.out::println);
	}

	private void exercise07() {
		/*
			Exercise 7 — Get a list of orders which were ordered on 15-Mar-2021, log the order records to the console
			and then return its product list You can see that this exercise involves two actions — (1)
			write order records to the console and (2) produce a list of products. Generating different output
			from a stream is not possible, how can we fulfill this requirement? Apart from running the stream flow twice,
			operation peek() allows execution of system logic as part of a stream flow.
			The sample solution runs peek() to write order records to the console right after data filtering,
			then subsequent operations such as flatMap() will be executed for the output of product records.
		 */
		var res = orderRepos.findAll()
						.stream()
						.filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021,3,15)))
						.peek(System.out::println)
						.flatMap(order -> order.getProducts().stream());

		res.forEach(System.out::println);


						// [[1 2, 3], [4, 5], [6]] --> [1, 2, 3, 4, 5, 6]
	}

	private void exercise08() {
		/*
			Calculate total lump sum of all orders placed in Feb 2021
			All previous exercise was to output a record list by a terminal
			operation, let’s do some calculation this time. This exercise is
			to sum up all the products ordered in Feb 2021. As you’ve gone through previous exercises,
			you can easily obtain the list of products using filter() and flatMap() operations. Next,
			you can make use of mapToDouble() operation to convert the stream into a stream of data type Double
			by specifying the price field as the mapping value. At last, terminal operation sum()
			will help you add up all values and return the total value
		 */
		var priceProducts = orderRepos.findAll()
						.stream()
						.filter(order -> order.getOrderDate().getYear() == 2021 && order.getOrderDate().getMonth().equals(Month.FEBRUARY))
//						.filter(new Predicate<Order>() {
//							@Override
//							public boolean test(Order order) {
//								return order.getOrderDate().getYear() == 2021 && order.getOrderDate().getMonth().equals(Month.FEBRUARY);
//							}
//						})
						.flatMap(order -> order.getProducts().stream())
						.mapToDouble(product -> product.getPrice())
						.sum();
		System.out.println(priceProducts);

	}

	private void exercise09(LocalDate localDate) {
		/*
			Calculate order average payment placed on 14-Mar-2021
			In addition to total sum, stream API offers operation for average value calculation as well.
			You might find that the return data type is different from sum() as it is an Optional data type. The reason
			is that the data stream would be empty and so calculation won’t output an average value
			for a empty data stream.
		 */


		var result = orderRepos.findAll()
						.stream()
						.filter(o->o.getOrderDate().isEqual(localDate))
						.flatMap(o->o.getProducts().stream())
						.mapToDouble(p->p.getPrice())
						.average();

		if(result.isEmpty()) {
			System.out.println("No products");
		}
		else {
			System.out.println("Result: "+result.getAsDouble());
		}
	}

	private void implementFilter() {
		var orders = orderRepos.findAll();
		CustomFilter<Order> orderCustomFilter = new CustomFilter<>();

		Comparator<Order> orderComparator = Comparator.comparing(Order::getId);

		/*
		var result = orderCustomFilter.filter(orders, new Predicate<Order>() {
			@Override
			public boolean test(Order order) {
				return order.getOrderDate().getMonth().equals(Month.FEBRUARY);
			}
		});
		 */
		var result = orderCustomFilter
						.filterJDKPredicate(orders, (order -> order.getOrderDate().getMonth().equals(Month.FEBRUARY)));

		var result2 = orderCustomFilter.filterHardcoded(orders);
		var result3 = orderCustomFilter.filterByMonth(orders, Month.FEBRUARY);
		var result3b = orderCustomFilter.filterByMonth(orders, Month.JANUARY);

		result.forEach(item -> System.out.println(item));
		System.out.println("Filter harcoded");
		result2.forEach(item -> System.out.println(item));
		System.out.println("Filter by month");
		result3.forEach(item -> System.out.println(item));

		var result4 = orderCustomFilter
						.filterByOrderPredicate
										(orders, order -> order.getOrderDate().getMonth().equals(Month.FEBRUARY));

		var result5 = orderCustomFilter
						.filterByOrderPredicate
										(orders, order -> order.getOrderDate().getMonth().equals(Month.JANUARY));

		var result6 = orderCustomFilter
						.filterByOrderPredicate
										(orders, order -> order.getId()%2==0);

		var result7 = orderCustomFilter
						.filterByMyPredicate(orders, order -> order.getCustomer().getId() == 1);

		var result8 = orderCustomFilter
						.filterByMyPredicate(orders, order -> order.getCustomer().getTier() == 1);

		var result9 = orderCustomFilter
				.filterJDKPredicate(orders, order -> order.getCustomer().getTier() == 2);

		System.out.println("---------------------");
		result9.forEach(item -> System.out.println(item));

	}
}
