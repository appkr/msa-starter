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

/**
 * With logback-console:
 * 2020-12-09 16:18:48.367  INFO [,f2d4fd90e7271eef,f2d4fd90e7271eef] 7704 --- [  XNIO-1 task-1] {{packageName}}.http.api              : {"origin":"remote","type":"request","correlation":"f2d4fd90e7271eef","protocol":"HTTP/1.1","remote":"0:0:0:0:0:0:0:1","method":"GET","uri":"http://localhost:{{portNumber}}/api/examples","headers":{"Accept":["*//*"],"Authorization":["user"],"Host":["localhost:{{portNumber}}"],"User-Agent":["curl/7.64.1"]}}
 * 2020-12-09 16:18:48.449  INFO [,f2d4fd90e7271eef,f2d4fd90e7271eef] 7704 --- [  XNIO-1 task-1] {{packageName}}.http.api              : {"origin":"local","type":"response","correlation":"f2d4fd90e7271eef","duration":102,"protocol":"HTTP/1.1","status":200,"headers":{"Authorization":["user"],"Cache-Control":["no-cache, no-store, max-age=0, must-revalidate"],"Connection":["keep-alive"],"Content-Type":["{{mediaType}}"],"Date":["Wed, 09 Dec 2020 07:18:48 GMT"],"Expires":["0"],"Pragma":["no-cache"],"Transfer-Encoding":["chunked"],"Vary":["Origin","Access-Control-Request-Method","Access-Control-Request-Headers"],"X-B3-Flags":["0"],"X-B3-Sampled":["0"],"X-B3-SpanId":["f2d4fd90e7271eef"],"X-B3-TraceId":["f2d4fd90e7271eef"],"X-Content-Type-Options":["nosniff"],"X-XSS-Protection":["1; mode=block"]},"body":{"data":[{"exampleId":1,"title":"original title","createdAt":"2020-12-09T16:18:48.406798+09:00","updatedAt":"2020-12-09T16:18:48.406865+09:00","createdBy":"user","updatedBy":"user"}],"page":{"size":1,"totalElements":1,"totalPages":1,"number":1}}}
 *
 * With logback-json:
 * {"@timestamp":"2020-12-09T16:18:48.367+09:00","level":"INFO","thread_name":"XNIO-1 task-1","logger_name":"{{packageName}}.http.api","message":"{\"origin\":\"remote\",\"type\":\"request\",\"correlation\":\"f2d4fd90e7271eef\",\"protocol\":\"HTTP/1.1\",\"remote\":\"0:0:0:0:0:0:0:1\",\"method\":\"GET\",\"uri\":\"http://localhost:{{portNumber}}/api/examples\",\"headers\":{\"Accept\":[\"*//*\"],\"Authorization\":[\"user\"],\"Host\":[\"localhost:{{portNumber}}\"],\"User-Agent\":[\"curl/7.64.1\"]}}","exception":null,"traceId":"f2d4fd90e7271eef","spanId":"f2d4fd90e7271eef"}
 * {"@timestamp":"2020-12-09T16:18:48.449+09:00","level":"INFO","thread_name":"XNIO-1 task-1","logger_name":"{{packageName}}.http.api","message":"{\"origin\":\"local\",\"type\":\"response\",\"correlation\":\"f2d4fd90e7271eef\",\"duration\":102,\"protocol\":\"HTTP/1.1\",\"status\":200,\"headers\":{\"Authorization\":[\"user\"],\"Cache-Control\":[\"no-cache, no-store, max-age=0, must-revalidate\"],\"Connection\":[\"keep-alive\"],\"Content-Type\":[\"{{mediaType}}\"],\"Date\":[\"Wed, 09 Dec 2020 07:18:48 GMT\"],\"Expires\":[\"0\"],\"Pragma\":[\"no-cache\"],\"Transfer-Encoding\":[\"chunked\"],\"Vary\":[\"Origin\",\"Access-Control-Request-Method\",\"Access-Control-Request-Headers\"],\"X-B3-Flags\":[\"0\"],\"X-B3-Sampled\":[\"0\"],\"X-B3-SpanId\":[\"f2d4fd90e7271eef\"],\"X-B3-TraceId\":[\"f2d4fd90e7271eef\"],\"X-Content-Type-Options\":[\"nosniff\"],\"X-XSS-Protection\":[\"1; mode=block\"]},\"body\":{\"data\":[{\"exampleId\":1,\"title\":\"original title\",\"createdAt\":\"2020-12-09T16:18:48.406798+09:00\",\"updatedAt\":\"2020-12-09T16:18:48.406865+09:00\",\"createdBy\":\"user\",\"updatedBy\":\"user\"}],\"page\":{\"size\":1,\"totalElements\":1,\"totalPages\":1,\"number\":1}}}","exception":null,"traceId":"f2d4fd90e7271eef","spanId":"f2d4fd90e7271eef"}
 */
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
