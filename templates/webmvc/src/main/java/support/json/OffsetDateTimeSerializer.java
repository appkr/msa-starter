package {{packageName}}.support.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@JsonComponent
public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {

  @Override
  public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    String result = StringUtils.EMPTY;
    try {
      result = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value);
    } catch (DateTimeException e) {
      result = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value);
    }
    gen.writeString(result);
  }
}
