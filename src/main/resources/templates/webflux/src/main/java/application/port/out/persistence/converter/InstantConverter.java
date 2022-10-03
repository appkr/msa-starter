package {{packageName}}.application.port.out.persistence.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class InstantConverter {

  @WritingConverter
  public static class WriteConverter implements Converter<Instant, LocalDateTime> {
    public LocalDateTime convert(Instant source) {
      return LocalDateTime.ofInstant(source, ZoneOffset.UTC);
    }
  }

  @ReadingConverter
  public static class ReadConverter implements Converter<LocalDateTime, Instant> {
    @Override
    public Instant convert(LocalDateTime localDateTime) {
      return localDateTime.toInstant(ZoneOffset.UTC);
    }
  }
}
