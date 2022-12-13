package {{packageName}}.application;

import static net.logstash.logback.argument.StructuredArguments.kv;

import {{packageName}}.application.port.out.PersistentEventRepository;
import {{packageName}}.domain.PersistentEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "application.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class PersistentEventPublisher {

  private final PersistentEventRepository repository;
  private final MessageProducer producer;

  @Transactional
  @Scheduled(fixedDelayString = "PT50S", initialDelayString = "PT10S")
  @SchedulerLock(name = "PersistentEventPublisher")
  @Async
  public void publish() {
    final Instant timeScope = Instant.now().minus(1, ChronoUnit.MINUTES);
    List<PersistentEvent> candidates = repository.findUnproducedByTimeScope(timeScope);
    if (candidates.isEmpty()) {
      return;
    }

    // CAVEAT
    // @Async has two limitations:
    //   - it must be applied to public methods only
    //   - self-invocation – calling the async method from within the same class – won't work
    // @see https://www.baeldung.com/spring-async#the-async-annotation
    writeLog("started", kv("total", candidates.size()));

    int success = 0;
    for (PersistentEvent candidate : candidates) {
      try {
        boolean produced = producer.produce(candidate);
        if (produced) {
          candidate.markProduced();
          success++;
          writeLog("handling",
              kv("persistentEventId", candidate.getId()),
              kv("eventType", candidate.getEventType()),
              kv("eventId", candidate.getEventId())
          );
        } else {
          throw new RuntimeException("Message was not produced");
        }
      } catch (Exception e) {
        candidate.markFailed();
        reportError(e, kv("persistentEventId", candidate.getId()));
      }
    }

    writeLog("success", kv("success", success), kv("total", candidates.size()));
  }

  private void writeLog(String event, Object... context) {
    log.info("PersistentEventPublisher#publish {} {}", event, context);
  }

  private void reportError(Exception e, Object... context) {
    log.info("PersistentEventPublisher#publish failed {} {}", context, e.getMessage());
  }
}
