package {{packageName}}.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

  private String version = "0.0.1.SNAPSHOT";

  private Scheduler scheduler = new Scheduler();

  @Getter
  @Setter
  class Scheduler {
    private Boolean enabled;
  }
}
