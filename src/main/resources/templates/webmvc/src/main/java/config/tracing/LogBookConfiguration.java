package {{packageName}}.config.tracing;

import static {{packageName}}.config.Constants.LogKey.*;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.zalando.logbook.HeaderFilters.authorization;

import {{packageName}}.support.JsonUtils;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.*;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;

@Configuration
public class LogBookConfiguration {

  static final Pattern JSON_PATTERN = Pattern.compile("application\\/.*json.*");
  static final Integer MAX_RESPONSE_LOG_LENGTH = 1_024;

  private final Tracer tracer;

  public LogBookConfiguration(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") Tracer tracer) {
    this.tracer = tracer;
  }

  @Bean
  public Logbook logBook() {
    return Logbook.builder()
        .condition(new CustomCondition())
        .headerFilter(authorization())
        .queryFilter(new CustomQueryFilter())
        .bodyFilter(new PrettyPrintingJsonBodyFilter())
        .correlationId(new CustomCorrelationId())
        .sink(new DefaultSink(new CustomHttpLogFormatter(), new CustomHttpLogWriter()))
        .build();
  }

  static boolean wantRawContent(HttpRequest request) {
    final String contentType = request.getContentType();
    return containsIgnoreCase(contentType, "octet-stream");
  }

  static boolean isPreflightRequest(HttpRequest request) {
    final String method = request.getMethod();
    return containsIgnoreCase(method, "OPTIONS") || containsIgnoreCase(method, "HEAD");
  }

  static boolean isWhitelistedPath(HttpRequest request) {
    final String path = request.getPath();
    return startsWithIgnoreCase(path, "/api/");
  }

  static boolean isBlacklistedPath(HttpRequest request) {
    final String path = request.getPath();
    return startsWithIgnoreCase(path, "/management/") || startsWithIgnoreCase(path, "/admin/");
  }

  static boolean wantJson(HttpRequest request) {
    final String contentType = request.getContentType();
    return contentType != null && JSON_PATTERN.matcher(contentType).matches();
  }

  static boolean produceJson(HttpResponse response) {
    final String contentType = response.getContentType();
    return contentType != null && JSON_PATTERN.matcher(contentType).matches();
  }

  static boolean isReadRequest(HttpRequest request) {
    final String method = request.getMethod();
    return containsIgnoreCase(method, "GET") || containsIgnoreCase(method, "HEAD");
  }

  class CustomCondition implements Predicate<HttpRequest> {
    @Override
    public boolean test(HttpRequest httpRequest) {
      if (wantRawContent(httpRequest)) {
        return false;
      }
      if (isPreflightRequest(httpRequest)) {
        return false;
      }
      if (isBlacklistedPath(httpRequest)) {
        return false;
      }
      if (!isWhitelistedPath(httpRequest)) {
        return false;
      }

      return true;
    }
  }

  class CustomQueryFilter implements QueryFilter {
    @Override
    public String filter(String query) {
      return URLDecoder.decode(query, Charset.defaultCharset());
    }
  }

  class CustomCorrelationId implements CorrelationId {
    @Override
    public String generate(HttpRequest request) {
      final Span currentSpan = tracer.currentSpan();
      if (currentSpan == null) {
        return new DefaultCorrelationId().generate(request);
      }

      return currentSpan.context().traceId();
    }
  }

  class CustomHttpLogFormatter implements HttpLogFormatter {
    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
      final Map<String, Object> reqLog = new HashMap<>();
      reqLog.put(ENDPOINT, request.getMethod() + " " + request.getRequestUri());

      if (wantJson(request)) {
        reqLog.put(BODY, JsonUtils.convertStringToJsonNode(request.getBodyAsString()));
      }

      return "HTTP Request\n" + JsonUtils.convertObjectToPrettyString(reqLog);
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
      final Map<String, Object> resLog = new HashMap<>();
      resLog.put(STATUS, response.getStatus());

      if (produceJson(response)) {
        resLog.put(BODY, JsonUtils.convertStringToJsonNode(response.getBodyAsString()));
      }

      return "HTTP Response\n" + JsonUtils.convertObjectToPrettyString(resLog);
    }
  }

  class CustomHttpLogWriter implements HttpLogWriter {

    private Logger log = LoggerFactory.getLogger(HTTP_INBOUND_LOGGER);

    @Override
    public boolean isActive() {
      return true;
    }

    @Override
    public void write(final Precorrelation precorrelation, final String request) {
      log.info(request);
    }

    @Override
    public void write(final Correlation correlation, final String response) {
      log.info(response);
    }
  }
}
