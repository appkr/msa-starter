package {{packageName}}.api.error;

import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BadRequestAlertException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 1L;
  private final String entityName;
  private final String errorKey;

  public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
    this(ExceptionTranslator.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
  }

  public BadRequestAlertException(URI type, String defaultMessage, String entityName, String errorKey) {
    super(type, defaultMessage, Status.BAD_REQUEST, null, null, null, getAlertParameters(entityName, errorKey));
    this.entityName = entityName;
    this.errorKey = errorKey;
  }

  private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("message", "error." + errorKey);
    parameters.put("params", entityName);
    return parameters;
  }
}
