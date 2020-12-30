package {{packageName}}.config.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import {{packageName}}.config.Constants;
import lombok.Builder;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

public class JsonLayout extends LayoutBase<ILoggingEvent> {

  private final ObjectMapper mapper;
  private static final String LINE_SEPARATOR = "\n";
  private static final int STACKTRACE_MAX_LENGTH = 5;

  public JsonLayout() {
    this.mapper = new ObjectMapper();
  }

  @Override
  public String doLayout(ILoggingEvent event) {
    FormatParameter parameter =
        FormatParameter.builder()
            .timestamp(getTimeStringFromTimestamp(event.getTimeStamp()))
            .level(String.valueOf(event.getLevel()))
            .threadName(event.getThreadName())
            .loggerName(event.getLoggerName())
            .message(event.getFormattedMessage())
            .exception(getThrowableProperty(event))
            .extraProperties(event.getMDCPropertyMap())
            .build();

    return getStringFromObjectMapper(parameter) + LINE_SEPARATOR;
  }

  private String getTimeStringFromTimestamp(long timestamp) {
    return OffsetDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp), ZoneId.of(Constants.TIMEZONE_SEOUL))
        .toString();
  }

  private String getThrowableProperty(ILoggingEvent event) {
    if (event == null) return null;

    IThrowableProxy tp = event.getThrowableProxy();
    if (tp == null) return null;

    return throwableProxyToString(tp);
  }

  private String throwableProxyToString(IThrowableProxy tp) {
    StringBuilder sb = new StringBuilder();

    recursiveAppend(sb, null, tp);

    return sb.toString();
  }

  private void recursiveAppend(StringBuilder sb, String prefix, IThrowableProxy tp) {
    if (tp == null) return;
    subjoinFirstLine(sb, prefix, tp);
    sb.append(CoreConstants.LINE_SEPARATOR);
    subjoinSTEPArray(sb, tp);
    IThrowableProxy[] suppressed = tp.getSuppressed();
    if (suppressed != null) {
      for (IThrowableProxy current : suppressed) {
        recursiveAppend(sb, CoreConstants.SUPPRESSED, current);
      }
    }
    recursiveAppend(sb, CoreConstants.CAUSED_BY, tp.getCause());
  }

  private void subjoinFirstLine(StringBuilder buf, String prefix, IThrowableProxy tp) {
    if (prefix != null) {
      buf.append(prefix);
    }
    subjoinExceptionMessage(buf, tp);
  }

  private void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
    buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
  }

  protected void subjoinSTEPArray(StringBuilder buf, IThrowableProxy tp) {
    StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
    int commonFrames = tp.getCommonFrames();

    boolean unrestrictedPrinting = STACKTRACE_MAX_LENGTH > stepArray.length;

    int maxIndex = (unrestrictedPrinting) ? stepArray.length : STACKTRACE_MAX_LENGTH;
    if (commonFrames > 0 && unrestrictedPrinting) {
      maxIndex -= commonFrames;
    }

    int ignoredCount = 0;
    for (int i = 0; i < maxIndex; i++) {
      StackTraceElementProxy element = stepArray[i];
      if (!isIgnoredStackTraceLine(element.toString())) {
        printStackLine(buf, ignoredCount, element);
        ignoredCount = 0;
        buf.append(CoreConstants.LINE_SEPARATOR);
      } else {
        ++ignoredCount;
        if (maxIndex < stepArray.length) {
          ++maxIndex;
        }
      }
    }
    if (ignoredCount > 0) {
      printIgnoredCount(buf, ignoredCount);
      buf.append(CoreConstants.LINE_SEPARATOR);
    }

    if (commonFrames > 0 && unrestrictedPrinting) {
      buf.append("... ")
          .append(tp.getCommonFrames())
          .append(" common frames omitted")
          .append(CoreConstants.LINE_SEPARATOR);
    }
  }

  private void printStackLine(StringBuilder buf, int ignoredCount, StackTraceElementProxy element) {
    buf.append(element);
    if (ignoredCount > 0) {
      printIgnoredCount(buf, ignoredCount);
    }
  }

  private void printIgnoredCount(StringBuilder buf, int ignoredCount) {
    buf.append(" [").append(ignoredCount).append(" skipped]");
  }

  private boolean isIgnoredStackTraceLine(String line) {
    return !line.contains("{{groupName}}");
  }

  private String getStringFromObjectMapper(Object message) {
    try {
      return mapper.writeValueAsString(message);
    } catch (Exception e) {
      addError(
          "Json Serialize failed.  Defaulting to message.toString().  Message: " + e.getMessage(),
          e);
      return message.toString();
    }
  }

  @Builder
  static class FormatParameter {
    // NOTE. LogstashEncoder format
    //
    // {"@timestamp":"2020-06-25T17:37:14.484+09:00","@version":"1","message":"...","logger_name":"...","thread_name":"main","level":"INFO"}
    // An log instance from this class will be serialized and set as the "message".
    // And then, the filebeat processor will decode the "message" and override the value of
    // logger_name, thread_name, etc, ...
    @JsonProperty("@timestamp")
    String timestamp;

    @JsonProperty("level")
    String level;

    @JsonProperty("thread_name")
    String threadName;

    @JsonProperty("logger_name")
    String loggerName;

    @JsonProperty("message")
    String message;

    @JsonProperty("exception")
    String exception;

    @JsonIgnore Map<String, String> extraProperties;
    @JsonAnyGetter
    @JsonSerialize(using = RawObjectKVSerializer.class)
    public Map<String, String> getMap() {
      return extraProperties;
    }

    @Override
    public String toString() {
      return String.format(
          "{\"level\":\"%s\",\"loggerName\":\"%s\",\"message\": \"%s\"}",
          level, loggerName, message);
    }
  }

  public static class RawObjectKVSerializer extends JsonSerializer<Map<String, String>> {
    @Override
    public void serialize(
        Map<String, String> map, JsonGenerator jgen, SerializerProvider serializerProvider)
        throws IOException {
      for (Map.Entry<String, String> e : map.entrySet()) {
        jgen.writeFieldName(e.getKey());
        if (e.getValue() == null) jgen.writeNull();
        else
          // Write value as raw data, since it's already JSON text
          jgen.writeObject(e.getValue());
      }
    }
  }
}
