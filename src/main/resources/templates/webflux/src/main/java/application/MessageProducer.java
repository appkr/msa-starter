/**
 * MessageSchema spec: @see https://wiki.mm.meshkorea.net/display/MES/Message+Schema
 */
package {{packageName}}.application;

import {{packageName}}.config.Constants.MessageKey;
import {{packageName}}.domain.PersistentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static {{packageName}}.config.Constants.PROJECT_NAME;
import static {{packageName}}.config.MessagingConfiguration.PRODUCER_CHANNEL;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

  private final StreamBridge streamBridge;

  public boolean produce(PersistentEvent persistentEvent) {
    final String body = persistentEvent.getBody();
    Message<?> message = MessageBuilder
        .withPayload(body)
        .setHeader(MessageKey.ID, persistentEvent.getEventId())
        .setHeader(MessageKey.TYPE, persistentEvent.getEventType())
        .setHeader(MessageKey.VERSION, 1)
        .setHeader(MessageKey.SOURCE, PROJECT_NAME)
        .setHeader(MessageKey.RESOURCE, body.getClass().getSimpleName())
        .setHeader(MessageKey.PARTITION_KEY, persistentEvent.getPartitionKey())
        .build();

    return streamBridge.send(PRODUCER_CHANNEL, message);
  }
}
