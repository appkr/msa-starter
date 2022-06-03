/**
 * @see https://www.baeldung.com/spring-scheduled-tasks
 * @see https://www.baeldung.com/spring-async#2-override-the-executor-at-the-application-level
 * @see https://www.baeldung.com/spring-boot-graceful-shutdown
 */
package {{packageName}}.config.scheduler;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
@ConditionalOnProperty(name = "application.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class SchedulerConfiguration implements AsyncConfigurer {

  private final TaskExecutionProperties properties;

  public SchedulerConfiguration(TaskExecutionProperties properties) {
    this.properties = properties;
  }

  @Bean
  @Override
  public Executor getAsyncExecutor() {
    final TaskExecutionProperties.Pool config = properties.getPool();

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(config.getCoreSize());
    executor.setMaxPoolSize(config.getMaxSize());
    executor.setQueueCapacity(config.getQueueCapacity());
    executor.setThreadNamePrefix("exe-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.setTaskDecorator(new AsyncTaskDecorator());

    return executor;
  }

  @Bean
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }
}
