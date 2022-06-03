package {{packageName}}.scheduled;

import static net.logstash.logback.argument.StructuredArguments.kv;

import {{packageName}}.repository.PersistentEventRepository;
import {{packageName}}.stream.MessageProducer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * When: a new record being created in persistent_event table
 *
 * Then:
 * ```
 * $ kafkactl consume local-example-output --from-beginning --print-headers
 * b3:c1fd9ecc53fc2d81-160976621383d9fb-0,
 * contentType:"application/json",
 * messageId:"cb970d49-465e-493b-8c1b-da90856412e0",
 * messageSource:example,
 * messageType:ExampleCreated,
 * messageVersion:1,
 * nativeHeaders:{"b3":["c1fd9ecc53fc2d81-160976621383d9fb-0"]},
 * partitionKey:"cb970d49-465e-493b-8c1b-da90856412e0",
 * resource:String,
 * scst_partition:0,
 * spring_json_header_types:{
 *   "b3": "java.lang.String",
 *   "nativeHeaders": "org.springframework.util.LinkedMultiValueMap",
 *   "messageType": "java.lang.String",
 *   "partitionKey": "java.util.UUID",
 *   "resource": "java.lang.String",
 *   "messageId": "java.util.UUID",
 *   "messageSource": "java.lang.String",
 *   "scst_partition": "java.lang.Integer",
 *   "messageVersion": "java.lang.Integer",
 *   "contentType": "java.lang.String"
 * }#{
 * "title": "original title"
 * }
 * ```
 */
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
  public Mono<Void> publish() {
    final Instant timeScope = Instant.now().minus(1, ChronoUnit.MINUTES);

    return repository.findUnproducedByTimeScope(timeScope)
        .flatMap(candidate -> {
          try {
            boolean produced = producer.produce(candidate);
            if (produced) {
              candidate.markProduced();
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

          return repository.save(candidate);
        })
        .then();
  }

  private void writeLog(String event, Object... context) {
    log.info("PersistentEventPublisher#publish {} {}", event, context);
  }

  private void reportError(Exception e, Object... context) {
    log.info("PersistentEventPublisher#publish failed {} {}", context, e.getMessage());
  }
}
