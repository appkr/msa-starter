package {{packageName}}.application.port.out;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import {{packageName}}.domain.Example;
import {{packageName}}.support.Carbon;

import java.util.List;

import static {{packageName}}.domain.QExample.example;

public class ExampleRepositoryImpl implements ExampleRepositoryCustom {

  private final JPAQueryFactory query;

  public ExampleRepositoryImpl(JPAQueryFactory query) {
    this.query = query;
  }

  @Override
  public List<Example> findCreatedToday() {
    return query.selectFrom(example)
        .where(withinToday())
        .fetch();
  }

  private BooleanExpression withinToday() {
    return example.createdAt.gt(Carbon.seoul().startOfDay().toInstant())
        .and(example.createdAt.lt(Carbon.seoul().endOfDay().toInstant()));
  }
}
