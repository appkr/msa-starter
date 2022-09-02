package {{packageName}}.support;

import static {{packageName}}.config.Constants.JwtKey.USER_ID_CLAIM;
import static {{packageName}}.config.Constants.UNKNOWN_USER_ID;

import {{packageName}}.adapter.in.rest.mapper.UUIDMapper;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class SecurityUtils {

  private SecurityUtils() {
  }

  public static Mono<Jwt> getPrincipal() {
    return ReactiveSecurityContextHolder.getContext()
        .map(securityContext -> securityContext.getAuthentication().getPrincipal())
        .cast(Jwt.class);
  }

  public static Mono<UUID> getCurrentUserId() {
    return getPrincipal()
        .map(principal -> {
          String userId = principal.getClaim(USER_ID_CLAIM);
          UUID uuid;
          try {
            uuid = new UUIDMapper().toUUID(userId);
          } catch (Exception e) {
            // NOTE. jhipster-uaa(Local)에서 발급한 토큰의 user_name 클레임 값은 "user"
            // vroong-uaa(EKS)에서 발급한 토큰의 user_name 클레임 값은 UUID type
            uuid = new UUIDMapper().toUUID(UNKNOWN_USER_ID);
          }
          return uuid;
        });
  }

  /**
   * Get the login of the current user.
   *
   * @return the login of the current user.
   */
  public static Mono<String> getCurrentUserLogin() {
    return ReactiveSecurityContextHolder
        .getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap(authentication -> Mono.justOrEmpty(extractPrincipal(authentication)));
  }

  private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
      return null;
    } else if (authentication.getPrincipal() instanceof UserDetails) {
      UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
      return springSecurityUser.getUsername();
    } else if (authentication.getPrincipal() instanceof String) {
      return (String) authentication.getPrincipal();
    }
    return null;
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise.
   */
  public static Mono<Boolean> isAuthenticated() {
    return ReactiveSecurityContextHolder
        .getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getAuthorities)
        .map(authorities -> authorities.stream().map(GrantedAuthority::getAuthority).noneMatch("ROLE_ANONYMOUS"::equals));
  }

  /**
   * Checks if the current user has any of the authorities.
   *
   * @param authorities the authorities to check.
   * @return true if the current user has any of the authorities, false otherwise.
   */
  public static Mono<Boolean> hasCurrentUserAnyOfAuthorities(String... authorities) {
    return ReactiveSecurityContextHolder
        .getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getAuthorities)
        .map(authorityList ->
            authorityList
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> Arrays.asList(authorities).contains(authority))
        );
  }

  /**
   * Checks if the current user has none of the authorities.
   *
   * @param authorities the authorities to check.
   * @return true if the current user has none of the authorities, false otherwise.
   */
  public static Mono<Boolean> hasCurrentUserNoneOfAuthorities(String... authorities) {
    return hasCurrentUserAnyOfAuthorities(authorities).map(result -> !result);
  }

  /**
   * Checks if the current user has a specific authority.
   *
   * @param authority the authority to check.
   * @return true if the current user has the authority, false otherwise.
   */
  public static Mono<Boolean> hasCurrentUserThisAuthority(String authority) {
    return hasCurrentUserAnyOfAuthorities(authority);
  }
}