/**
 * Enum 타입은 tinyint로 저장한다
 * @see https://meshkorea.slack.com/archives/G601YP5C2/p1606975067314900?thread_ts=1586244933.087600&cid=G601YP5C2
 *
 * insert
 * into
 *     persistent_event
 *     (id, body, created_at, event_id, event_type, partition_key, produced_at, status)
 * values
 *     (null, ?, ?, ?, ?, ?, ?, ?)
 * binding parameter [1] as [VARCHAR] - [{"title":"original title"}]
 * binding parameter [2] as [TIMESTAMP] - [2020-12-08T06:15:30.192140Z]
 * binding parameter [3] as [VARCHAR] - [9f3ef3e6-d22f-45df-8290-6730a743d931]
 * binding parameter [4] as [VARCHAR] - [ExampleCreated]
 * binding parameter [5] as [VARCHAR] - [ddc1e4ba-ca03-4b2d-943c-ad8a59705b8a]
 * binding parameter [6] as [TIMESTAMP] - [null]
 * binding parameter [7] as [INTEGER] - [10]
 */
package {{packageName}}.repository.converter;

import {{packageName}}.domain.PersistentEventStatus;
import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PersistentEventStatusConverter implements AttributeConverter<PersistentEventStatus, Integer> {

  @Override
  public Integer convertToDatabaseColumn(PersistentEventStatus status) {
    if (status == null) {
      return null;
    }
    return status.getCode();
  }

  @Override
  public PersistentEventStatus convertToEntityAttribute(Integer code) {
    if (code == null) {
      return null;
    }

    return Stream.of(PersistentEventStatus.values())
        .filter(c -> c.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
