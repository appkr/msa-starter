package {{packageName}}.application.port.in.exception;

import {{packageName}}.domain.FieldError;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ConstraintViolationProblem extends RuntimeException {

  private URI type = URI.create("about:blank");

  private String title;

  private Integer status;

  private String detail;

  private URI instance;

  private Collection<FieldError> fieldErrors = new ArrayList<>();

  public ConstraintViolationProblem() {
  }

  public ConstraintViolationProblem(String message) {
    super(message);
  }

  public ConstraintViolationProblem(String message, Collection<FieldError> fieldErrors) {
    super(message);
    this.fieldErrors = fieldErrors;
  }

  public void add(FieldError fieldError) {
    this.fieldErrors.add(fieldError);
  }
}
