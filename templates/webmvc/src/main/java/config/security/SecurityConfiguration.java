package {{packageName}}.config.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri;

@Configuration
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

  private final OAuth2ClientProperties properties;
  private final SecurityProblemSupport problemSupport;

  public SecurityConfiguration(OAuth2ClientProperties properties, SecurityProblemSupport problemSupport) {
    this.properties = properties;
    this.problemSupport = problemSupport;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
        .cors(customizer -> customizer.and())
        .csrf(spec -> spec.disable())
        .headers(spec -> spec.frameOptions().disable())
        .sessionManagement(spec -> spec.sessionCreationPolicy(STATELESS))
        .authorizeRequests(spec ->
            spec.requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().authenticated())
        .oauth2ResourceServer(customizer -> customizer.jwt())
        .exceptionHandling(spec ->
            spec.authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport))
        .build();
    // @formatter:on
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return withJwkSetUri(properties.getProvider().get("uaa").getJwkSetUri()).build();
  }

  @Bean
  @Profile("test")
  public UserDetailsService users() {
    UserDetails user = User.builder()
        .username("user")
        .password(passwordEncoder().encode("user"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  @Profile("test")
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
