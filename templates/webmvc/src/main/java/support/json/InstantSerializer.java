package {{packageName}}.support.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@JsonComponent
public class InstantSerializer extends JsonSerializer<Instant> {

  @Override
  public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    String result = StringUtils.EMPTY;
    try {
      result = DateTimeFormatter.ISO_OFFSET_DATE_TIME
          .withZone(ZoneId.systemDefault())
          .format(value.truncatedTo(ChronoUnit.SECONDS));
    } catch (DateTimeException e) {
      result = DateTimeFormatter.ISO_LOCAL_DATE_TIME
          .withZone(ZoneId.systemDefault())
          .format(value.truncatedTo(ChronoUnit.SECONDS));
    }
    gen.writeString(result);
  }
}
