package {{packageName}}.config.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
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
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends ResourceServerConfigurerAdapter {

  private final SecurityProblemSupport problemSupport;

  public WebSecurityConfiguration(SecurityProblemSupport problemSupport) {
    this.problemSupport = problemSupport;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        // To avoid an OPTIONS request always making a 401 response:
        // @see https://www.baeldung.com/spring-security-cors-preflight
        .cors()
      .and()
        .csrf().disable()
        .headers().frameOptions().disable()
      .and()
        .sessionManagement().sessionCreationPolicy(STATELESS)
      .and()
        .authorizeRequests()
        .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
        .antMatchers("/api/**").authenticated()
      .and()
        .exceptionHandling()
        .authenticationEntryPoint(problemSupport)
        .accessDeniedHandler(problemSupport)
    ;
    // @formatter:on
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
