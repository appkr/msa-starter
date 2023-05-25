package {{packageName}}.support;

import org.apache.commons.lang3.time.StopWatch;

public class Timer {

  static TimerAdapter adapter = new TimerAdapter();

  public static void start() {
    adapter.start();
  }

  public static StopWatch stop() {
    StopWatch timer = null;
    try {
      timer = adapter.stop();
    } catch (Exception ignored) {
      // Null Object Pattern
      timer = new StopWatch();
      timer.start();
      timer.stop();
    }

    adapter.clear();

    return timer;
  }

  public static class TimerAdapter {

    final ThreadLocal<StopWatch> holder = new ThreadLocal<>();

    public void start() {
      clear();

      StopWatch value = holder.get();
      if (value != null && value.isStarted()) {
        throw new IllegalStateException("Timer가 이미 작동중입니다");
      }

      value = new StopWatch();
      value.start();

      holder.set(value);
    }

    public StopWatch stop() {
      StopWatch value = holder.get();
      if (value == null || !value.isStarted()) {
        throw new IllegalStateException("Timer가 작동중이지 않습니다");
      }

      value.stop();

      return value;
    }

    public void clear() {
      holder.set(null);
    }
  }
}
