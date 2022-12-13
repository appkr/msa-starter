package {{packageName}}.config;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@Slf4j
public class MessagingConfiguration {

  public static final String PRODUCER_CHANNEL = "produceMessage-out-0";
  public static final String CONSUMER_CHANNEL = "consumeMessage-in-0";

  @Bean
  public Consumer<Message<?>> consumeMessage() {
    return data -> {
      // Add business logic here
      log.info("A message received: {}", data.getPayload());
    };
  }
}
