package {{packageName}}.support;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class HeaderUtils {

  /**
   * build URI object from current request context
   *
   * @param template  "/{param1}/singer/{parms2}"
   * @param params    "ca58a4b3-8c6c-48db-bd30-9847404f3373", "c5276ac1-bdf9-4317-9bec-7d96f09cb44c"
   * @return          given current request context is https://localhost/api/albums
   *                  then "https://localhost/albums/ca58a4b3-8c6c-48db-bd30-9847404f3373/singer/c5276ac1-bdf9-4317-9bec-7d96f09cb44c"
   */
  public static URI uri(String template, Object... params) {
    return ServletUriComponentsBuilder.fromCurrentRequest().path(template).buildAndExpand(params).toUri();
  }

  private HeaderUtils() {
  }
}
