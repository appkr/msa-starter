package {{packageName}}.config.logging;

import brave.Span;
import brave.Tracer;
import {{packageName}}.config.Constants.LogKey;
import {{packageName}}.support.SecurityUtils;
import java.util.Collections;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.DefaultCorrelationId;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;

@Configuration
public class LogBookConfiguration {

  private final Tracer tracer;

  public LogBookConfiguration(Tracer tracer) {
    this.tracer = tracer;
  }

  @Bean
  public Logbook logBook() {
    return Logbook.builder()
        .condition(new Predicate<HttpRequest>() {
          @Override
          public boolean test(HttpRequest httpRequest) {
            final String contentType = httpRequest.getContentType();
            if (StringUtils.containsIgnoreCase(contentType, "octet-stream")) {
              return false;
            }

            final String method = httpRequest.getMethod();
            if (StringUtils.containsIgnoreCase(method, "OPTIONS")
                || StringUtils.containsIgnoreCase(method, "HEAD")) {
              return false;
            }

            final String path = httpRequest.getPath();
            if (StringUtils.startsWithIgnoreCase(path, "/management/")) {
              return false;
            }
            if (! StringUtils.startsWithIgnoreCase(path, "/api/")) {
              return false;
            }

            return true;
          }
        })
        .headerFilter(new HeaderFilter() {
          @Override
          public HttpHeaders filter(HttpHeaders headers) {
            return headers.update("Authorization",
                Collections.singletonList(SecurityUtils.getCurrentUserLogin().orElseGet(() -> "redacted")));
          }
        })
        .bodyFilter(new PrettyPrintingJsonBodyFilter())
        .correlationId(new CorrelationId() {
          @Override
          public String generate(HttpRequest request) {
            final Span currentSpan = tracer.currentSpan();
            if (currentSpan == null) {
              return new DefaultCorrelationId().generate(request);
            }

            return currentSpan.context().traceIdString();
          }
        })
        .sink(new DefaultSink(new JsonHttpLogFormatter(), new CustomHttpLogWriter()))
        .build();
  }

  class CustomHttpLogWriter implements HttpLogWriter {

    private Logger log = LoggerFactory.getLogger(LogKey.HTTP_INBOUND_LOGGER);

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
