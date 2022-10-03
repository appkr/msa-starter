package {{packageName}}.config.logging;

import static {{packageName}}.config.Constants.HeaderKey.B3_SPAN_ID;
import static {{packageName}}.config.Constants.HeaderKey.B3_TRACE_ID;

import brave.Span;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.autoconfig.SingleSkipPattern;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
public class SleuthTraceFilter implements WebFilter {

  private final Tracer tracer;
  private final SingleSkipPattern skipPattern;

  public SleuthTraceFilter(Tracer tracer,
      @Qualifier("defaultSkipPatternBean") SingleSkipPattern skipPattern) {
    this.tracer = tracer;
    this.skipPattern = skipPattern;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    final Span currentSpan = this.tracer.currentSpan();
    final Pattern pattern = skipPattern.skipPattern().get();
    final String currentPath = exchange.getRequest().getPath().pathWithinApplication().value();
    if (currentSpan != null && !pattern.matcher(currentPath).find()) {
      final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add(B3_TRACE_ID, currentSpan.context().traceIdString());
      map.add(B3_SPAN_ID, currentSpan.context().spanIdString());

      exchange.getResponse().getHeaders().addAll(map);
    }

    return chain.filter(exchange);
  }
}
