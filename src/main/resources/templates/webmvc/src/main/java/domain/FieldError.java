package {{packageName}}.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = {"objectName", "field", "message"})
public class FieldError implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  private String objectName;

  @ToString.Include
  private Object field;

  @ToString.Include
  private String message;

  public FieldError() {
  }

  public FieldError(String dto, String field, String message) {
    this.objectName = dto;
    this.field = field;
    this.message = message;
  }

  public FieldError(Object field, String message) {
    this.objectName = null;
    this.field = field;
    this.message = message;
  }
}
