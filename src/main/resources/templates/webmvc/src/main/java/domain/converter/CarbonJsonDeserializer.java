package {{packageName}}.domain.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import {{packageName}}.domain.Carbon;
import java.io.IOException;

public class CarbonJsonDeserializer  extends JsonDeserializer<Carbon> {

  @Override
  public Carbon deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return (p.getValueAsString() != null)
        ? Carbon.parse(p.getValueAsString())
        : null;
  }
}
