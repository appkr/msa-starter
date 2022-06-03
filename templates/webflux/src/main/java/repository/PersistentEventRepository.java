package {{packageName}}.repository;

import {{packageName}}.domain.PersistentEvent;
import {{packageName}}.domain.PersistentEventStatus;
import java.time.Instant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PersistentEventRepository extends ReactiveCrudRepository<PersistentEvent, Long> {

  @Query("SELECT * FROM persistent_events e WHERE e.createdAt >= :timeScope "
      + "AND e.status = {{packageName}}.domain.PersistentEventStatus.CREATED")
  Flux<PersistentEvent> findUnproducedByTimeScope(@Param("timeScope") Instant timeScope);

  @Query("SELECT * FROM persistent_events e WHERE e.status = :status")
  Flux<PersistentEvent> findByStatus(@Param("status") PersistentEventStatus status);
}
