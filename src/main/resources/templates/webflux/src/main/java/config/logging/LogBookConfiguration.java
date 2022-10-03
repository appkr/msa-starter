package {{packageName}}.config.logging;

import static {{packageName}}.config.Constants.LogKey.BODY;
import static {{packageName}}.config.Constants.LogKey.ENDPOINT;
import static {{packageName}}.config.Constants.LogKey.HTTP_INBOUND_LOGGER;
import static {{packageName}}.config.Constants.LogKey.STATUS;
import static org.zalando.logbook.HeaderFilters.authorization;
import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

import brave.Span;
import brave.Tracer;
import {{packageName}}.support.JsonUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.autoconfig.SingleSkipPattern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.DefaultCorrelationId;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;

@Configuration
public class LogBookConfiguration {

  static final Pattern JSON_PATTERN = Pattern.compile("application\\/.*json.*");
  private final Tracer tracer;
  private final SingleSkipPattern skipPattern;

  public LogBookConfiguration(Tracer tracer, @Qualifier("defaultSkipPatternBean") SingleSkipPattern skipPattern) {
    this.tracer = tracer;
    this.skipPattern = skipPattern;
  }

  @Bean
  public Logbook logBook() {
    return Logbook.builder()
        .condition(request -> {
          final String contentType = request.getContentType();
          if (StringUtils.containsIgnoreCase(contentType, "octet-stream")) {
            return false;
          }

          final String method = request.getMethod();
          if (StringUtils.containsIgnoreCase(method, "OPTIONS")
              || StringUtils.containsIgnoreCase(method, "HEAD")
              || StringUtils.containsIgnoreCase(method, "GET")) {
            return false;
          }

          final Pattern pattern = skipPattern.skipPattern().get();
          final String path = request.getPath();
          if (pattern.matcher(path).find()) {
            return false;
          }
          if (! StringUtils.startsWithIgnoreCase(path, "/api/")) {
            return false;
          }

          return true;
        })
        .headerFilter(authorization())
        .bodyFilter(jsonPath("$.password").replace("***"))
        .bodyFilter(jsonPath("$.accessToken").replace("***"))
        .bodyFilter(jsonPath("$.refreshToken").replace("***"))
        .bodyFilter(jsonPath("$.access_token").replace("***"))
        .bodyFilter(jsonPath("$.refresh_token").replace("***"))
        .bodyFilter(new PrettyPrintingJsonBodyFilter())
        .correlationId(request -> {
          final Span currentSpan = tracer.currentSpan();
          if (currentSpan == null) {
            return new DefaultCorrelationId().generate(request);
          }

          return currentSpan.context().traceIdString();
        })
        .sink(new DefaultSink(new CustomHttpLogFormatter(), new CustomHttpLogWriter()))
        .build();
  }

  class CustomHttpLogFormatter implements HttpLogFormatter {
    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
      HashMap<String, Object> reqLog = new HashMap<>();
      reqLog.put(ENDPOINT, request.getMethod() + " " + request.getRequestUri());

      final String contentType = request.getContentType();
      if (contentType != null && JSON_PATTERN.matcher(contentType).matches()) {
        reqLog.put(BODY, JsonUtils.convertStringToJsonNode(request.getBodyAsString()));
      }

      return "HTTP Request\n" + JsonUtils.convertObjectToPrettyString(reqLog);
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
      HashMap<String, Object> resLog = new HashMap<>();
      resLog.put(STATUS, response.getStatus());

      final String contentType = response.getContentType();
      if (contentType != null && JSON_PATTERN.matcher(contentType).matches()) {
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
