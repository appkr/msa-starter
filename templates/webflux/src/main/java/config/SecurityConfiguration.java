package {{packageName}}.config;

import java.security.GeneralSecurityException;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

  private final OAuth2ClientProperties properties;
  private final SecurityProblemSupport problemSupport;

  public SecurityConfiguration(OAuth2ClientProperties properties, SecurityProblemSupport problemSupport) {
    this.properties = properties;
    this.problemSupport = problemSupport;
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws GeneralSecurityException {
    // @formatter:off
    return http
        .cors(customizer -> customizer.and())
        .csrf(spec -> spec.disable())
        .headers(spec -> spec.frameOptions().disable())
        .authorizeExchange(spec -> {
          spec.pathMatchers("/management/health", "/management/health/**", "/management/info").permitAll()
              .pathMatchers("/management/**").authenticated()
              .pathMatchers("/api/**").authenticated()
              .anyExchange().authenticated();
        })
        .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
        .exceptionHandling(spec -> {
          spec.authenticationEntryPoint(problemSupport)
              .accessDeniedHandler(problemSupport);
        })
        .build();
    // @formatter:on
  }

  @Bean
  @Primary
  public ReactiveJwtDecoder reactiveJwtDecoder() {
    return new NimbusReactiveJwtDecoder(properties.getProvider().get("uaa").getJwkSetUri());
  }
}
