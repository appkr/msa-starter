package {{packageName}}.application.port.out.persistence.converter;

import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class UUIDConverter {

  @WritingConverter
  public static class WriteConverter implements Converter<UUID, String> {
    @Override
    public String convert(UUID source) {
      if (source == null) {
        return null;
      }

      return source.toString();
    }
  }

  @ReadingConverter
  public static class ReadConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(String source) {
      if (source == null) {
        return null;
      }

      return UUID.fromString(source);
    }
  }
}

