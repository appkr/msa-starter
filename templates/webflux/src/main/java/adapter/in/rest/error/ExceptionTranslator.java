package {{packageName}}.adapter.in.rest.error;

import static {{packageName}}.config.Constants.PROJECT_NAME;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.*;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {

  public static final String ERR_VALIDATION = "error.validation";
  public static final URI DEFAULT_TYPE = URI.create(PROJECT_NAME + "/problem-with-message");
  public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROJECT_NAME + "/constraint-violation");
  public static final URI ERR_CONCURRENCY_FAILURE = URI.create(PROJECT_NAME + "/concurrency-failure");

  private static final String FIELD_ERRORS_KEY = "fieldErrors";
  private static final String MESSAGE_KEY = "message";
  private static final String PATH_KEY = "path";
  private static final String VIOLATIONS_KEY = "violations";

  @Override
  public ProblemBuilder prepare(final Throwable throwable, final StatusType status, final URI type) {
    final StatusType recalculatedStatus = recalculateStatus(throwable, status);
    if (throwable instanceof HttpMessageConversionException) {
      return Problem.builder()
          .withType(type)
          .withTitle(status.getReasonPhrase())
          .withStatus(recalculatedStatus)
          .withDetail("Unable to convert http message")
          .withCause(Optional
              .ofNullable(throwable.getCause())
              .filter(cause -> isCausalChainsEnabled())
              .map(this::toProblem)
              .orElse(null));
    }

    if (throwable instanceof DataAccessException) {
      return Problem
          .builder()
          .withType(type)
          .withTitle(recalculatedStatus.getReasonPhrase())
          .withStatus(recalculatedStatus)
          .withDetail("Failure during data access")
          .withCause(Optional
              .ofNullable(throwable.getCause())
              .filter(cause -> isCausalChainsEnabled())
              .map(this::toProblem)
              .orElse(null));
    }

    if (containsPackageName(throwable.getMessage())) {
      return Problem
          .builder()
          .withType(type)
          .withTitle(recalculatedStatus.getReasonPhrase())
          .withStatus(recalculatedStatus)
          .withDetail("Unexpected runtime exception")
          .withCause(Optional
              .ofNullable(throwable.getCause())
              .filter(cause -> isCausalChainsEnabled())
              .map(this::toProblem)
              .orElse(null)
          );
    }

    return Problem
        .builder()
        .withType(type)
        .withTitle(recalculatedStatus.getReasonPhrase())
        .withStatus(recalculatedStatus)
        .withDetail(throwable.getMessage())
        .withCause(Optional
            .ofNullable(throwable.getCause())
            .filter(cause -> isCausalChainsEnabled())
            .map(this::toProblem)
            .orElse(null)
        );
  }

  @Override
  public Mono<ResponseEntity<Problem>> process(ResponseEntity<Problem> entity, ServerWebExchange request) {
    if (entity == null) {
      return Mono.empty();
    }

    Problem problem = entity.getBody();
    if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
      return Mono.just(entity);
    }

    ProblemBuilder builder = Problem
        .builder()
        .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? DEFAULT_TYPE : problem.getType())
        .withStatus(problem.getStatus())
        .withTitle(problem.getTitle())
        .with(PATH_KEY, request.getRequest().getPath().value());

    if (problem instanceof ConstraintViolationProblem) {
      builder
          .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
          .with(MESSAGE_KEY, ERR_VALIDATION);
    } else {
      builder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail())
          .withInstance(problem.getInstance());
      problem.getParameters().forEach(builder::with);
      if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
        builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
      }
    }

    return Mono.just(new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode()));
  }

  @Override
  public Mono<ResponseEntity<Problem>> handleBindingResult(WebExchangeBindException ex, ServerWebExchange request) {
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result
        .getFieldErrors()
        .stream()
        .map(f ->
            new FieldError(
                f.getObjectName().replaceFirst("Dto$", ""),
                f.getField(),
                StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode()
            )
        )
        .collect(Collectors.toList());

    Problem problem = Problem
        .builder()
        .withType(CONSTRAINT_VIOLATION_TYPE)
        .withTitle("Data binding and validation failure")
        .withStatus(Status.BAD_REQUEST)
        .with(MESSAGE_KEY, ERR_VALIDATION)
        .with(FIELD_ERRORS_KEY, fieldErrors)
        .build();

    return create(ex, problem, request);
  }

  @ExceptionHandler
  public Mono<ResponseEntity<Problem>> handle(BadRequestAlertException ex, ServerWebExchange request) {
    return create(ex, request);
  }

  @ExceptionHandler({ConcurrencyFailureException.class, DataIntegrityViolationException.class})
  public Mono<ResponseEntity<Problem>> handle(Exception ex, ServerWebExchange request) {
    Problem problem = Problem.builder()
        .withStatus(Status.CONFLICT)
        .withDetail("Concurrency or DataIntegrity related failure")
        .with(MESSAGE_KEY, ERR_CONCURRENCY_FAILURE)
        .build();
    return create(ex, problem, request);
  }

  private boolean containsPackageName(String message) {
    return StringUtils.containsAny(message, "org.", "java.", "net.", "javax.", "com.", "io.", "de.");
  }

  StatusType recalculateStatus(Throwable throwable, StatusType a) {
    StatusType b = a;
    if (throwable instanceof WebClientResponseException) {
      // NOTE throwable의 상태 코드는 4xx 인데, status는 5xx인 경우를 우회하기 위한 코드
      b = new StatusType() {
        WebClientResponseException clientError = (WebClientResponseException) throwable;
        @Override
        public int getStatusCode() {
          return clientError.getRawStatusCode();
        }

        @Override
        public String getReasonPhrase() {
          return clientError.getStatusText();
        }
      };
    }

    return b;
  }
}
