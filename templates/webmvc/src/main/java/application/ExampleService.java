package {{packageName}}.application;

import {{packageName}}.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ExampleService {

  public Page<Example> listExamples(Pageable pageable) {
    final Example entity = Example.create("original title");
    return new PageImpl<>(Collections.singletonList(entity), pageable, 1L);
  }
}
