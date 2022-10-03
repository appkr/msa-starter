package {{packageName}}.config.logging;

import brave.Span;
import brave.Tracer;
import {{packageName}}.config.Constants.HeaderKey;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class SleuthTraceFilter implements Filter {

  private final Tracer tracer;

  public SleuthTraceFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Span currentSpan = this.tracer.currentSpan();
    if (currentSpan == null) {
      chain.doFilter(request, response);
      return;
    }

    HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.addHeader(HeaderKey.B3_TRACE_ID, currentSpan.context().traceIdString());
    httpResponse.addHeader(HeaderKey.B3_SPAN_ID, currentSpan.context().spanIdString());
    httpResponse.addHeader(HeaderKey.B3_PARENT_ID, currentSpan.context().parentIdString());
    httpResponse.addHeader(HeaderKey.B3_SAMPLED, currentSpan.context().sampled() ? "1" : "0");
    httpResponse.addHeader(HeaderKey.B3_FLAGS, currentSpan.context().debug() ? "1" : "0");

    chain.doFilter(request, response);
  }
}
