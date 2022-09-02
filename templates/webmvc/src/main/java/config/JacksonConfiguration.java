package {{packageName}}.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  @Bean
  public JsonNullableModule jsonNullableModule() {
    return new JsonNullableModule();
  }

  @Bean
  public JavaTimeModule javaTimeModule() {
    return new JavaTimeModule();
  }
}
