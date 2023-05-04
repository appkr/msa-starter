package {{packageName}}.support;

import static {{packageName}}.config.Constants.JwtKey.USER_ID_CLAIM;
import static {{packageName}}.config.Constants.UNKNOWN_USER_ID;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

  /**
   * Get the login of the current user.
   *
   * @return the login of the current user.
   */
  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> {
          final Object principal = authentication.getPrincipal();
          if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
          } else if (principal instanceof String) {
            return (String) principal;
          } else if (principal instanceof Jwt jwt) {
            // 타입 오류를 피하기 위해 UUID 타입만 허용
            try {
              final String expectToBeUUID = jwt.getClaimAsString(USER_ID_CLAIM);
              UUID.fromString(expectToBeUUID);
              return expectToBeUUID;
            } catch (Exception ignored) {
            }
          }

          return UNKNOWN_USER_ID;
        });
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise.
   */
  public static boolean isAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> authentication.getAuthorities().stream()
            .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS")))
        .orElse(false);
  }

  /**
   * If the current user has a specific authority (security role).
   * <p>
   * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
   *
   * @param authority the authority to check.
   * @return true if the current user has the authority, false otherwise.
   */
  public static boolean isCurrentUserInRole(String authority) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
        .orElse(false);
  }

  private SecurityUtils() {
  }
}
