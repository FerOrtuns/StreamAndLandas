package space.gavinklfong.demo.streamapi.filter;

@FunctionalInterface
public interface MyPredicate<T> {

  boolean test(T t);
}
