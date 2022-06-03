package {{packageName}};

import static {{packageName}}.config.Constants.JwtKey.AUTHORITIES_CLAIM;
import static {{packageName}}.config.Constants.JwtKey.USER_ID_CLAIM;
import static {{packageName}}.config.Constants.UNKNOWN_USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import {{packageName}}.support.SecurityUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.test.StepVerifier;

@IntegrationTest
public class WithMockJwtTest {

  @Test
  @WithMockJwt
  void testDefault() {
    SecurityUtils.getPrincipal()
        .as(StepVerifier::create)
        .assertNext(jwt -> {
          assertEquals(UNKNOWN_USER_ID, jwt.getClaim(USER_ID_CLAIM));
          assertEquals("web-app", ((List<String>)jwt.getClaim("scope")).get(0));
          assertEquals(0, ((List<String>)jwt.getClaim(AUTHORITIES_CLAIM)).size());
        })
        .verifyComplete();
  }

  @Test
  @WithMockJwt(userId = "foo", authorities = { "ROLE_ADMIN", "ROLE_ANONYMOUS" })
  void testCustom() {
    SecurityUtils.getPrincipal()
        .as(StepVerifier::create)
        .assertNext(jwt -> {
          assertEquals("foo", jwt.getClaim(USER_ID_CLAIM));
          assertEquals(2, ((List<String>)jwt.getClaim(AUTHORITIES_CLAIM)).size());
        })
        .verifyComplete();
  }

  @Test
  @WithMockJwt
  void testDefault_imperativeStyle() {
    final Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    assertEquals(0, ((List<String>)jwt.getClaim(AUTHORITIES_CLAIM)).size());
  }

  @Test
  @WithMockJwt(userId = "foo", authorities = { "ROLE_ADMIN", "ROLE_ANONYMOUS" })
  void testCustom_imperativeStyle() {
    final Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    assertEquals("foo", jwt.getClaim(USER_ID_CLAIM));
    assertEquals(2, ((List<String>)jwt.getClaim(AUTHORITIES_CLAIM)).size());
  }
}
