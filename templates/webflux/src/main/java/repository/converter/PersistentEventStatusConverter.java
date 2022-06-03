package {{packageName}}.repository.converter;

import {{packageName}}.domain.PersistentEventStatus;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class PersistentEventStatusConverter {

  @WritingConverter
  public static class WriteConverter implements Converter<PersistentEventStatus, Integer> {
    @Override
    public Integer convert(PersistentEventStatus status) {
      if (status == null) {
        return null;
      }
      return status.getCode();
    }
  }

  @ReadingConverter
  public static class ReadConverter implements Converter<Integer, PersistentEventStatus> {
    @Override
    public PersistentEventStatus convert(Integer code) {
      if (code == null) {
        return null;
      }

      return Stream.of(PersistentEventStatus.values())
          .filter(c -> c.getCode().equals(code))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
  }
}
