package {{packageName}}.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfiguration {

  private final ObjectMapper mapper;

  public WebClientConfiguration(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Bean
  public ExchangeStrategies exchangeStrategies() {
    return ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper)))
        .build();
  }

  @Bean
  public ClientHttpConnector clientHttpConnector() {
    HttpClient httpClient = HttpClient.create()
        .wiretap(this.getClass().getCanonicalName(), LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
    return new ReactorClientHttpConnector(httpClient);
  }

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder()
        .exchangeStrategies(exchangeStrategies())
        .clientConnector(clientHttpConnector());
  }
}
