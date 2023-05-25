package {{packageName}}.support;

import org.junit.jupiter.api.Test;

public class LazyUsage {

  @Test
  void lazy() {
    Lazy.of(() -> compute(42))
        .map(i -> compute(i, 13))
        .flatMap(i -> lazyCompute(i, 15))
        .filter(i -> i > 0)
        .get()
        .ifPresent(System.out::println);
  }

  Integer compute(Integer in) {
    return in + 1;
  }

  Integer compute(Integer a, Integer b) {
    return a + b;
  }

  Lazy<Integer> lazyCompute(Integer a, Integer b) {
    return Lazy.of(() -> a + b);
  }
}
