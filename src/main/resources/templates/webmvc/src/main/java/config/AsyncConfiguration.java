package {{packageName}}.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

  final TaskExecutionProperties properties;

  public AsyncConfiguration(TaskExecutionProperties properties) {
    this.properties = properties;
  }

  @Bean
  public Executor defaultExecutor() {
    final TaskExecutionProperties.Pool config = properties.getPool();

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(config.getCoreSize());
    executor.setMaxPoolSize(config.getMaxSize());
    executor.setQueueCapacity(config.getQueueCapacity());
    executor.setThreadNamePrefix("executor-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.setTaskDecorator(taskDecorator());
    executor.initialize();

    // 참고: SecurityContext를 전달하기 위한 랩퍼
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }

  @Bean
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }

  TaskDecorator taskDecorator() {
    return runnable -> {
      final Map<String, String> contextMap = MDC.getCopyOfContextMap();
      return () -> {
        MDC.setContextMap(contextMap);
        try {
          runnable.run();
        } finally {
          MDC.clear();
        }
      };
    };
  }
}
