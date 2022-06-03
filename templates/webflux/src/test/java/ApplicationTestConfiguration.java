package {{packageName}};

import static {{packageName}}.config.Constants.JwtKey.AUTHORITIES_CLAIM;
import static {{packageName}}.config.Constants.JwtKey.USER_ID_CLAIM;
import static {{packageName}}.config.Constants.UNKNOWN_USER_ID;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

@TestConfiguration
@Profile("test")
public class ApplicationTestConfiguration {

  @Bean
  public ReactiveJwtDecoder testReactiveJwtDecoder() {
    return token -> {
      final Jwt jwt = new Jwt("header.payload.signature",
          Instant.now(),
          Instant.MAX,
          Map.of("alg", "RS256", "typ", "JWT"),
          Map.of("scope", List.of("web-app"), AUTHORITIES_CLAIM, List.of("ROLE_USER"), USER_ID_CLAIM, UNKNOWN_USER_ID));

      return Mono.just(jwt);
    };
  }
}
