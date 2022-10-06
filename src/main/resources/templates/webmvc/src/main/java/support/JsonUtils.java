package {{packageName}}.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;

import java.io.IOException;

public class JsonUtils {

  private static ObjectMapper mapper = new ObjectMapper();
  static {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new JsonNullableModule());

    mapper.disable(
        SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
    );
  }

  public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
    return mapper.writeValueAsBytes(object);
  }

  public static String convertObjectToString(Object object) throws IOException {
    return mapper.writeValueAsString(object);
  }

  public static String convertObjectToPrettyString(Object object) throws IOException {
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
  }

  public static JsonNode convertStringToJsonNode(String json) throws JsonProcessingException {
    return mapper.readTree(json);
  }

  private JsonUtils() {
  }
}
