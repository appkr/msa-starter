package {{packageName}}.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.WebExceptionHandler;
import org.zalando.problem.spring.webflux.advice.ProblemExceptionHandler;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {

  private ObjectMapper objectMapper;
  private ProblemHandling problemHandling;

  public WebFluxConfiguration(@Qualifier("jacksonObjectMapper") ObjectMapper objectMapper,
      ProblemHandling problemHandling) {
    this.objectMapper = objectMapper;
    this.problemHandling = problemHandling;
  }

  @Override
  public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
    registrar.setUseIsoFormat(true);
    registrar.registerFormatters(registry);
  }

  @Bean
  @Order(-2)
  // The handler must have precedence over WebFluxResponseStatusExceptionHandler and Spring Boot's ErrorWebExceptionHandler
  public WebExceptionHandler problemExceptionHandler() {
    return new ProblemExceptionHandler(objectMapper, problemHandling);
  }

  @Bean
  HandlerMethodArgumentResolver reactivePageableHandlerMethodArgumentResolver() {
    return new ReactivePageableHandlerMethodArgumentResolver();
  }

  @Bean
  HandlerMethodArgumentResolver reactiveSortHandlerMethodArgumentResolver() {
    return new ReactiveSortHandlerMethodArgumentResolver();
  }
}
