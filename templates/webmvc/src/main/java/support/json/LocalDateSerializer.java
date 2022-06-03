package {{packageName}}.support.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonComponent
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

  @Override
  public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    String result = StringUtils.EMPTY;
    try {
      result = DateTimeFormatter.ISO_OFFSET_DATE.format(ZonedDateTime.of(value.atTime(0, 0, 0), ZoneId.systemDefault()));
    } catch (DateTimeException e) {
      result = DateTimeFormatter.ISO_LOCAL_DATE.format(value);
    }
    gen.writeString(result);
  }
}
