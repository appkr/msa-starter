package {{packageName}}.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{packageName}}.domain.Example;
import {{packageName}}.domain.PersistentEvent;
import {{packageName}}.support.JsonUtils;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PersistentEventRepositoryTest {

  @Autowired
  private PersistentEventRepository repository;
  private PersistentEvent base;

  @BeforeEach
  @Transactional
  public void setup() throws IOException {
    final Example eventSource = Example.newInstance("original title");
    final String body = JsonUtils.convertObjectToString(eventSource);
    final PersistentEvent entity = PersistentEvent.newInstance("ExampleCreated", UUID.randomUUID(), body);
    this.base = repository.saveAndFlush(entity);
  }

  @Test
  @Transactional(readOnly = true)
  public void testFindUnproduced() {
    // when
    final Instant timeScope = Instant.now().minus(1, ChronoUnit.MINUTES);
    List<PersistentEvent> candidates = repository.findUnproducedByTimeScope(timeScope);

    // then
    assertTrue(candidates.size() > 0);
    assertEquals(this.base, candidates.get(0));

    // SQL:
    // select *
    // from persistent_event persistent0_
    // where
    //     created_at>=?
    //     and status=10
    // binding parameter [1] as [TIMESTAMP] - [2020-12-08T06:14:30.324195Z]
  }
}