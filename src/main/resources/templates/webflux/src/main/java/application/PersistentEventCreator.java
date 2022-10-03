package {{packageName}}.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import {{packageName}}.domain.PersistentEvent;
import {{packageName}}.application.port.out.persistence.PersistentEventRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersistentEventCreator {

  private final PersistentEventRepository repository;
  private final ObjectMapper objectMapper;

  @Transactional
  public Mono<Void> create(String eventType, Object source) {
    String body = "";
    try {
      body = objectMapper.writeValueAsString(source);
    } catch (IOException e) {
      log.error("Serialization failed", e);
    }
    final PersistentEvent entity = PersistentEvent.newInstance(eventType, UUID.randomUUID(), body);

    return repository.save(entity).then();
  }
}
