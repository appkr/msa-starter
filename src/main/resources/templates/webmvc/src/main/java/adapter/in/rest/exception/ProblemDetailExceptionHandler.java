package {{packageName}}.adapter.in.rest.exception;

import static {{packageName}}.config.Constants.PROJECT_NAME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import {{packageName}}.application.port.in.exception.ConstraintViolationProblem;
import {{packageName}}.domain.FieldError;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(HttpClientErrorException.class)
  public final ProblemDetail handleConstraintViolationProblem(HttpClientErrorException ex, WebRequest request) {
    final ConstraintViolationProblem problem = ex.getResponseBodyAs(ConstraintViolationProblem.class);
    return create(BAD_REQUEST, problem.getMessage(), problem.getFieldErrors());
  }

  @ExceptionHandler(ConstraintViolationProblem.class)
  public final ProblemDetail handleConstraintViolationProblem(ConstraintViolationProblem ex, WebRequest request) {
    return create(BAD_REQUEST, ex.getMessage(), ex.getFieldErrors());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public final ProblemDetail handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    final List<FieldError> fieldErrors = ex.getConstraintViolations().stream()
        .map(v -> new FieldError(null, v.getPropertyPath().toString(), v.getMessage()))
        .toList();

    return create(BAD_REQUEST, ex.getMessage(), fieldErrors);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    final List<FieldError> fieldErrors = ex.getFieldErrors().stream()
        .map(f -> new FieldError(f.getObjectName(), f.getField(), f.getCode()))
        .toList();

    return ResponseEntity
        .status(status)
        .body(create(BAD_REQUEST, ex.getMessage(), fieldErrors));
  }

  private ProblemDetail create(HttpStatus status, String message) {
    return create(status, message, Collections.emptyList());
  }

  private ProblemDetail create(HttpStatus status, String message, Collection<FieldError> fieldErrors) {
    final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
    problemDetail.setType(URI.create(PROJECT_NAME + "/constraint-violation"));
    problemDetail.setTitle("Method argument not valid");
    problemDetail.setProperty("message", "error.validation");
    if (!fieldErrors.isEmpty()) {
      problemDetail.setProperty("fieldErrors", fieldErrors);
    }

    return problemDetail;
  }
}
