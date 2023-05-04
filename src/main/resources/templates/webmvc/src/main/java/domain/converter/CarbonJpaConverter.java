package {{packageName}}.domain.converter;

import {{packageName}}.domain.Carbon;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.ZoneId;

@Converter(autoApply = true)
public class CarbonJpaConverter implements AttributeConverter<Carbon, Instant> {

  @Override
  public Instant convertToDatabaseColumn(Carbon attribute) {
    if (attribute == null) {
      return null;
    }

    return attribute.toInstant();
  }

  @Override
  public Carbon convertToEntityAttribute(Instant dbData) {
    if (dbData == null) {
      return null;
    }

    return Carbon.from(dbData, ZoneId.systemDefault());
  }
}
