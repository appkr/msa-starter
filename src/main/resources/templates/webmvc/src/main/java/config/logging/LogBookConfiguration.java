package {{packageName}}.config.logging;

import static {{packageName}}.config.Constants.LogKey.*;
import static {{packageName}}.config.Constants.LogKey.STATUS;
import static org.zalando.logbook.HeaderFilters.authorization;
import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

import brave.Span;
import brave.Tracer;
import {{packageName}}.support.JsonUtils;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.autoconfig.SingleSkipPattern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.logbook.*;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;

@Configuration
public class LogBookConfiguration {

  static final String BLACKLISTED_CONTENT_TYPE = "octet-stream";
  static final List<String> BLACKLISTED_METHODS = List.of("HEAD", "OPTIONS", "head", "options");
  static final Pattern WHITELISTED_PATH_PATTERN = Pattern.compile("(\\/api\\/).+");
  static final Pattern WHITELISTED_CONTENT_TYPE_PATTERN = Pattern.compile("application\\/.*json.*");

  static final Logger inboundLogger = LoggerFactory.getLogger(HTTP_INBOUND_LOGGER);
  static final Logger outboundLogger = LoggerFactory.getLogger(HTTP_OUTBOUND_LOGGER);

  private final Tracer tracer;
  private final SingleSkipPattern skipPattern;

  public LogBookConfiguration(Tracer tracer, @Qualifier("defaultSkipPatternBean") SingleSkipPattern skipPattern) {
    this.tracer = tracer;
    this.skipPattern = skipPattern;
  }

  @Primary
  @Bean
  public Logbook inboundLogbook() {
    final HttpLogFormatter formatter = new CustomHttpLogFormatter("HTTP Request", "HTTP Response");
    final HttpLogWriter writer = new CustomHttpLogWriter(inboundLogger);

    return logbookBuilder()
        .sink(new DefaultSink(formatter, writer))
        .build();
  }

  @Bean
  public Logbook outboundLogbook() {
    final HttpLogFormatter formatter = new CustomHttpLogFormatter("OUTBOUND Request", "OUTBOUND Response");
    final HttpLogWriter writer = new CustomHttpLogWriter(outboundLogger);

    return logbookBuilder()
        .sink(new DefaultSink(formatter, writer))
        .build();
  }

  LogbookCreator.Builder logbookBuilder() {
    return Logbook.builder()
        .condition(new CustomCondition())
        .queryFilter(new CustomQueryFilter())
        .headerFilter(authorization())
        .bodyFilter(jsonPath("$.password").replace("***"))
        .bodyFilter(jsonPath("$.accessToken").replace("***"))
        .bodyFilter(jsonPath("$.refreshToken").replace("***"))
        .bodyFilter(jsonPath("$.access_token").replace("***"))
        .bodyFilter(jsonPath("$.refresh_token").replace("***"))
        .bodyFilter(new PrettyPrintingJsonBodyFilter())
        .correlationId(new CustomCorrelationId());
  }

  class CustomCondition implements Predicate<HttpRequest> {
    @Override
    public boolean test(HttpRequest httpRequest) {
      final String contentType = httpRequest.getContentType();
      if (StringUtils.containsIgnoreCase(contentType, BLACKLISTED_CONTENT_TYPE)) {
        return false;
      }

      final String method = httpRequest.getMethod();
      if (BLACKLISTED_METHODS.contains(method)) {
        return false;
      }

      final String path = httpRequest.getPath();
      if (!WHITELISTED_PATH_PATTERN.matcher(path).matches()) {
        return false;
      }
      if (skipPattern.skipPattern().get().matcher(path).matches()) {
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

      return currentSpan.context().traceIdString();
    }
  }

  class CustomHttpLogFormatter implements HttpLogFormatter {

    private final String reqHeader;
    private final String resHeader;

    public CustomHttpLogFormatter(String reqHeader, String resHeader) {
      this.reqHeader = reqHeader;
      this.resHeader = resHeader;
    }

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
      final Map<String, Object> reqLog = new HashMap<>();
      reqLog.put(ENDPOINT, request.getMethod() + " " + request.getRequestUri());

      final String contentType = request.getContentType();
      if (contentType != null && WHITELISTED_CONTENT_TYPE_PATTERN.matcher(contentType).matches()) {
        reqLog.put(BODY, JsonUtils.convertStringToJsonNode(request.getBodyAsString()));
      }

      return reqHeader + "\n" + JsonUtils.convertObjectToPrettyString(reqLog);
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
      final Map<String, Object> resLog = new HashMap<>();
      resLog.put(STATUS, response.getStatus());

      final String contentType = response.getContentType();
      if (contentType != null && WHITELISTED_CONTENT_TYPE_PATTERN.matcher(contentType).matches()) {
        resLog.put(BODY, JsonUtils.convertStringToJsonNode(response.getBodyAsString()));
      }

      return resHeader + "\n" + JsonUtils.convertObjectToPrettyString(resLog);
    }
  }

  class CustomHttpLogWriter implements HttpLogWriter {

    Logger log;

    public CustomHttpLogWriter(Logger log) {
      this.log = log;
    }

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
