package {{packageName}}.config.scheduler;

import {{packageName}}.support.Timer;
import java.util.concurrent.ThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.CustomizableThreadCreator;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "application.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class SchedulerConfiguration implements SchedulingConfigurer {

  private final TaskExecutionProperties properties;

  public SchedulerConfiguration(TaskExecutionProperties properties) {
    this.properties = properties;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    final TaskExecutionProperties.Pool config = properties.getPool();
    final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setThreadFactory(new SchedulerThreadFactory("scheduler-"));
    scheduler.setPoolSize(config.getCoreSize());
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(10);
    scheduler.initialize();

    taskRegistrar.setTaskScheduler(scheduler);
  }

  static class SchedulerThreadFactory extends CustomizableThreadCreator implements ThreadFactory {

    public SchedulerThreadFactory(String threadNamePrefix) {
      super(threadNamePrefix);
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
      return createThread(() -> {
        Timer.start();
        MDC.setContextMap(MDC.getCopyOfContextMap());
        try {
          runnable.run();
        } finally {
          MDC.clear();
          Timer.stop();
        }
      });
    }
  }
}
