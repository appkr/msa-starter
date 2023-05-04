package {{packageName}}.config.tracing;

import io.sentry.CustomSamplingContext;
import io.sentry.SentryOptions.TracesSamplerCallback;
import io.sentry.spring.boot.jakarta.SentryProperties;
import io.sentry.spring.jakarta.tracing.SentryTracingFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryPerformanceConfiguration {

  private static final Pattern PATTERN_SAMPLING_WHITELIST = Pattern.compile("^/api/.*$");

  @Bean
  public FilterRegistrationBean<SentryTracingFilter> sentryTracingFilter(){
    final FilterRegistrationBean<SentryTracingFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new SentryTracingFilter());

    return registrationBean;
  }

  @Bean
  public TracesSamplerCallback tracesSamplerCallback(SentryProperties properties) {
    return context -> {
      final CustomSamplingContext customSamplingContext = context.getCustomSamplingContext();

      if (customSamplingContext != null) {
        final HttpServletRequest request = (HttpServletRequest) customSamplingContext.get("request");
        final Matcher matcher = PATTERN_SAMPLING_WHITELIST.matcher(request.getRequestURI());

        if (matcher.matches()) {
          return properties.getSampleRate();
        }
      }

      return 0.0;
    };
  }
}
