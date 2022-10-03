package {{packageName}}.adapter.in.rest.mapper;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDMapper {

  public String toString(UUID uuid) {
    if (uuid == null) {
      return null;
    }

    return uuid.toString();
  }

  public UUID toUUID(String string) {
    if (string == null) {
      return null;
    }

    return UUID.fromString(string);
  }
}
