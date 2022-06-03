package {{packageName}}.repository;

import {{packageName}}.domain.Example;
import java.util.List;

public interface ExampleRepositoryCustom {

  List<Example> findCreatedToday();
}
