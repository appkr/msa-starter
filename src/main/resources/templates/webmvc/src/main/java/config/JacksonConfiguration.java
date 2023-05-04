package {{packageName}}.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import {{packageName}}.domain.Carbon;
import {{packageName}}.domain.converter.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Bean
  public JsonNullableModule jsonNullableModule() {
    return new JsonNullableModule();
  }

  @Bean
  public JavaTimeModule javaTimeModule() {
    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());

    return javaTimeModule;
  }

  @Bean
  public ObjectMapper jacksonObjectMapper(JavaTimeModule javaTimeModule, JsonNullableModule jsonNullableModule) {
    final SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer(Carbon.class, new CarbonJsonSerializer());
    simpleModule.addDeserializer(Carbon.class, new CarbonJsonDeserializer());

    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModules(javaTimeModule, jsonNullableModule, simpleModule);

    mapper.disable(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    mapper.disable(
        SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        SerializationFeature.FAIL_ON_EMPTY_BEANS);

    return mapper;
  }

  static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return (p.getValueAsString() != null)
          ? LocalDate.parse(p.getValueAsString(), LOCAL_DATE_FORMATTER)
          : null;
    }
  }

  static class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == null) {
        return;
      }

      gen.writeString(value.format(LOCAL_DATE_FORMATTER));
    }
  }
}
