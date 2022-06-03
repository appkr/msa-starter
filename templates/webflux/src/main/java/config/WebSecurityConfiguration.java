package {{packageName}}.config;

import java.security.GeneralSecurityException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SecurityProblemSupport.class)
public class WebSecurityConfiguration {

  private final SecurityProblemSupport problemSupport;

  public WebSecurityConfiguration(SecurityProblemSupport problemSupport) {
    this.problemSupport = problemSupport;
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws GeneralSecurityException {
    // @formatter:off
    return http
        // To avoid an OPTIONS request always making a 401 response:
        // @see https://www.baeldung.com/spring-security-cors-preflight
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
  @Profile("test")
  public MapReactiveUserDetailsService users() {
    UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("user"))
        .roles("USER")
        .build();

    return new MapReactiveUserDetailsService(user);
  }

  @Bean
  @Profile("test")
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
