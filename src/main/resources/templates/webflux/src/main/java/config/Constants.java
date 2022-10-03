package {{packageName}}.config;

public class Constants {

  public static final String PROJECT_NAME = "{{projectName}}";
  public static final String SYSTEM_ACCOUNT = "system";
  public static final String UNKNOWN_USER_ID = "00000000-0000-0000-0000-000000000000";
  public static final String V1_MEDIA_TYPE = "{{mediaType}}";

  public static final String TIMEZONE_SEOUL = "UTC+9";
  public static final int TIMEZONE_SEOUL_HOURS = 9;

  public static class Profile {
    public static final String TEST_PROFILE = "test";
    public static final String LOCAL_PROFILE = "local";
    public static final String DEV_PROFILE = "dev";
    public static final String QA_PROFILE = "qa";
    public static final String PROD_PROFILE = "prod";
  }

  public static class HeaderKey {
    public static final String B3_TRACE_ID = "X-B3-TraceId";
    public static final String B3_SPAN_ID = "X-B3-SpanId";
    public static final String B3_PARENT_ID = "X-B3-ParentSpanId";
    public static final String B3_SAMPLED = "X-B3-Sampled";
    public static final String B3_FLAGS = "X-B3-Flags";
  }

  /**
   * MessageSchema spec: @see https://wiki.mm.meshkorea.net/display/MES/Message+Schema
   */
  public static class MessageKey {
    public static final String ID = "messageId";
    public static final String TYPE = "messageType";
    public static final String VERSION = "messageVersion";
    public static final String SOURCE = "messageSource";
    public static final String RESOURCE = "resource";
    public static final String PARTITION_KEY = "partitionKey";
  }

  public static class MessagePolicy {
    public static final Long DEFAULT_TIMEOUT = 5000L; // milliseconds
  }

  public static class LogKey {
    public static final String HTTP_INBOUND_LOGGER = "{{packageName}}.http.api";
    public static final String HTTP_OUTBOUND_LOGGER = "{{packageName}}.http.external";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String USER_NAME = "userName";
    public static final String STATUS = "status";
    public static final String ENDPOINT = "endpoint";
    public static final String HEADERS = "headers";
    public static final String BODY = "body";
  }

  public static class JwtKey {
    public static final String USER_ID_CLAIM = "user_name";
    public static final String AUTHORITIES_CLAIM = "authorities";
  }

  private Constants() {
  }
}
