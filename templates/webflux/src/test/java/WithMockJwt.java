package {{packageName}};

import static {{packageName}}.config.Constants.JwtKey.AUTHORITIES_CLAIM;
import static {{packageName}}.config.Constants.JwtKey.USER_ID_CLAIM;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwt.Factory.class)
public @interface WithMockJwt {

  String userId() default "00000000-0000-0000-0000-000000000000";
  String[] authorities() default {};

  class Factory implements WithSecurityContextFactory<WithMockJwt> {
    @Override
    public SecurityContext createSecurityContext(WithMockJwt annotation) {
      // JWT 토큰을 만든다
      final String tokenValue = "token";
      final Instant issuedAt = Instant.now();
      final Instant expiresAt = Instant.MAX;
      final Map<String, Object> headers = Map.of("alg", "RS256", "typ", "JWT");
      final Map<String, Object> claims = Map.of("scope", List.of("web-app"), AUTHORITIES_CLAIM, List.of(annotation.authorities()), USER_ID_CLAIM, annotation.userId());

      // SecurityContext를 적용한다
      final Jwt jwt = new Jwt(tokenValue, issuedAt, expiresAt, headers, claims);
      final Authentication authentication = new JwtAuthenticationToken(jwt);
      authentication.setAuthenticated(true);

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);

      return context;
    }
  }
}
