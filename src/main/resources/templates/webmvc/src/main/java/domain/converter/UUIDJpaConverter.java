package {{packageName}}.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public class UUIDJpaConverter implements AttributeConverter<UUID, String> {

  @Override
  public String convertToDatabaseColumn(UUID attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.toString();
  }

  @Override
  public UUID convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return UUID.fromString(dbData);
  }
}
