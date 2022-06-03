package {{packageName}}.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

  private SecurityUtils() {
  }

  /**
   * Get the login of the current user.
   *
   * @return the login of the current user.
   */
  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> {
          if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
          } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
          }
          return null;
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

  public static void changeContext(UUID userId) {
    final AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(
        "system",
        userId,
        new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    SecurityContextHolder.getContext().setAuthentication(token);
  }

  public static void resetContext() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}