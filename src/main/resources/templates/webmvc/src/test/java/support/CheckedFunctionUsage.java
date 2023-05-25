package {{packageName}}.support;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class CheckedFunctionUsage {

  @Test
  void useWrap() {
    final Exception e = assertThrows(Exception.class, () -> {
      Stream.of("foo", "bar", "baz", "qux")
          .map(CheckedFunction.wrap(name -> doSomething(name)))
          .forEach(System.out::println);
    });

    e.printStackTrace();
  }

  String doSomething(String name) throws Exception {
    if (name.equals("bar")) {
      throw new Exception();
    }

    return name.toUpperCase();
  }
}
