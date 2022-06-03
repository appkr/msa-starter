package {{packageName}}.config;

import io.r2dbc.spi.ConnectionFactory;
import java.util.Map;
import java.util.concurrent.Executor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.r2dbc.R2dbcLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
@ConditionalOnProperty(name = "application.scheduler.enabled", havingValue = "true", matchIfMissing = false)
@EnableSchedulerLock(defaultLockAtMostFor = "PT50S")
public class SchedulerConfiguration {

  private final TaskExecutionProperties properties;
  private final ConnectionFactory connectionFactory;

  public SchedulerConfiguration(TaskExecutionProperties properties, ConnectionFactory connectionFactory) {
    this.properties = properties;
    this.connectionFactory = connectionFactory;
  }

  @Bean
  public LockProvider lockProvider() {
    return new R2dbcLockProvider(connectionFactory);
  }

  @Bean
  @Primary
  public Executor defaultTaskExecutor() {
    final TaskExecutionProperties.Pool config = properties.getPool();

    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(config.getCoreSize());
    executor.setMaxPoolSize(config.getMaxSize());
    executor.setQueueCapacity(config.getQueueCapacity());
    executor.setThreadNamePrefix("exe-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.initialize();

    return executor;
  }

  @Bean
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }

  static class AsyncTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      if (contextMap == null) {
        /**
         * WORKAROUND for "Exception in thread "exe-3" java.lang.NullPointerException
         *   at {{packageName}}.config.scheduler.AsyncTaskDecorator.lambda$decorate$0(AsyncTaskDecorator.java:21)"
         * @see https://jira.qos.ch/browse/LOGBACK-944
         */
        MDC.put("foo", "bar");
      }

      return () -> {
        MDC.setContextMap(contextMap);
        try {
          runnable.run();
        } finally {
          MDC.clear();
        }
      };
    }
  }
}
