package {{packageName}};

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;

@Configuration
@Profile("test")
@Slf4j
public class IntegrationTestConfiguration {

  @Bean
  @Primary
  public Executor testExecutor() {
    return new SyncTaskExecutor();
  }
}
