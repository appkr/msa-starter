/**
 * For DomainEvent spec: @see https://wiki.mm.meshkorea.net/display/MES/Event+Schema
 * To secure at-least-once publishing: @see https://wiki.mm.meshkorea.net/pages/viewpage.action?pageId=62645748
 */
package {{packageName}}.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("persistent_events")
@Data
public class PersistentEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  private UUID eventId = UUID.randomUUID();

  private String eventType;

  private UUID partitionKey;

  private String body;

  private Instant createdAt = Instant.now();

  private Instant producedAt;

  private PersistentEventStatus status = PersistentEventStatus.CREATED;

  protected PersistentEvent() {}

  private PersistentEvent(String eventType, UUID partitionKey, String body) {
    this.eventType = eventType;
    this.partitionKey = partitionKey;
    this.body = body;
  }

  public static PersistentEvent newInstance(String eventType, UUID partitionKey, String body) {
    return new PersistentEvent(eventType, partitionKey, body);
  }

  public void markProduced() {
    this.status = PersistentEventStatus.PRODUCED;
    this.producedAt = Instant.now();
  }

  public void markFailed() {
    this.status = PersistentEventStatus.FAILED;
  }
}
