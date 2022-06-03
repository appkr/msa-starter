package {{packageName}}.support.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@JsonComponent
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    try {
      return LocalDate.parse(p.getText(), DateTimeFormatter.ISO_OFFSET_DATE);
    } catch (DateTimeParseException e) {
      return LocalDate.parse(p.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
  }
}
