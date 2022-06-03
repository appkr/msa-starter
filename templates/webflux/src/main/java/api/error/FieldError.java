package {{packageName}}.api.error;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class FieldError implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String objectName;
  private final String field;
  private final String message;

  public FieldError(String dto, String field, String message) {
    this.objectName = dto;
    this.field = field;
    this.message = message;
  }
}
