package {{packageName}}.support;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class EitherUsage {

  @Test
  void useEither() {
    Stream.of("foo", "bar", "baz", "qux")
        .map(Either.lift(name -> doSomething(name)))
        .forEach(System.out::println);
  }

  @Test
  void useEitherWithValue() {
    Stream.of("foo", "bar", "baz", "qux")
        .map(Either.liftWithValue(name -> doSomething(name)))
        .forEach(System.out::println);
  }

  String doSomething(String name) throws Exception {
    if (name.equals("bar")) {
      throw new Exception();
    }

    return name.toUpperCase();
  }
}
