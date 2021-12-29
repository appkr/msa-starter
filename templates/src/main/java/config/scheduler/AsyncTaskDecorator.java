package {{packageName}}.config.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
class AsyncTaskDecorator implements TaskDecorator {

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
      StopWatch stopWatch = new StopWatch();
      try {
        stopWatch.start();
        runnable.run();
      } finally {
        stopWatch.stop();
        log.info("A scheduled task finished, elapsedTime={}", stopWatch.getTime(TimeUnit.SECONDS));
        MDC.clear();
      }
    };
  }
}
