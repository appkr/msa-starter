package {{packageName}}.config.tracing;

import static {{packageName}}.config.Constants.HeaderKey.*;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class BackwardCompatibleTraceFilter implements Filter {

  final Tracer tracer;

  public BackwardCompatibleTraceFilter(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final Span currentSpan = tracer.currentSpan();
    if (currentSpan == null) {
      chain.doFilter(request, response);
      return;
    }

    // For backward compatibility
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.addHeader(B3_TRACE_ID, currentSpan.context().traceId());
    httpResponse.addHeader(B3_SPAN_ID, currentSpan.context().spanId());
    httpResponse.addHeader(B3_PARENT_ID, currentSpan.context().parentId());
    // NOTE: io.micrometer.tracing.TraceContext.sampled() can be null in test context
    httpResponse.addHeader(B3_SAMPLED,
        (currentSpan.context().sampled() != null && currentSpan.context().sampled()) ? "1" : "0");
    httpResponse.addHeader(B3_FLAGS, "0");

    if (currentSpan.context().parentId() != null) {
      // build a w3c format
      // @see https://www.w3.org/TR/trace-context/#parent-id
      // Vendors MUST ignore the traceparent when the parent-id is invalid (for example, if it contains non-lowercase hex characters).
      final String traceParent = String.format("00-%s-%s-%02d", currentSpan.context().traceId(),
          currentSpan.context().parentId(), currentSpan.context().sampled() ? 1 : 0);
      final String traceState = String.format("%s-%s", W3C_TRACE_STATE_VENDOR_KEY, currentSpan.context().parentId());

      httpResponse.addHeader(W3C_TRACE_PARENT, traceParent);
      httpResponse.addHeader(W3C_TRACE_STATE, traceState);
    }

    chain.doFilter(request, response);
  }
}
