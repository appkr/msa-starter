package {{packageName}}.config.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

  private final OAuth2ClientProperties properties;

  public SecurityConfiguration(OAuth2ClientProperties properties) {
    this.properties = properties;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    return http
        .cors(customizer -> customizer.and())
        .csrf(spec -> spec.disable())
        .headers(spec -> spec.frameOptions().disable())
        .sessionManagement(spec -> spec.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(spec ->
            spec.requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
                .requestMatchers("/management/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated())
        .oauth2ResourceServer(customizer -> customizer.jwt())
        .build();
    // @formatter:on
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return withJwkSetUri(properties.getProvider().get("uaa").getJwkSetUri()).build();
  }

  @Bean
  public JwtAuthenticationConverter customJwtAuthenticationConverter() {
    final JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    authoritiesConverter.setAuthorityPrefix("");
    authoritiesConverter.setAuthoritiesClaimName("authorities");

    final JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

    return authenticationConverter;
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
