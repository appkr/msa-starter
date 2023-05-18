package {{packageName}}.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.ReflectionUtils;

public final class TestUtils {

  private static final ObjectMapper mapper = createObjectMapper();

  private static ObjectMapper createObjectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new JsonNullableModule());

    mapper.disable(
        SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
    );

    return mapper;
  }

  /**
   * Convert an object to JSON byte array.
   *
   * @param object the object to convert.
   * @return the JSON byte array.
   * @throws IOException
   */
  public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
    return mapper.writeValueAsBytes(object);
  }

  public static String convertObjectToString(Object object) throws IOException {
    return mapper.writeValueAsString(object);
  }

  /**
   * Create a byte array with a specific size filled with specified data.
   *
   * @param size the size of the byte array.
   * @param data the data to put in the byte array.
   * @return the JSON byte array.
   */
  public static byte[] createByteArray(int size, String data) {
    byte[] byteArray = new byte[size];
    for (int i = 0; i < size; i++) {
      byteArray[i] = Byte.parseByte(data, 2);
    }
    return byteArray;
  }

  /**
   * Create a {@link FormattingConversionService} which use ISO date format, instead of the localized one.
   * @return the {@link FormattingConversionService}.
   */
  public static FormattingConversionService createFormattingConversionService() {
    final DefaultFormattingConversionService dfcs = new DefaultFormattingConversionService ();
    final DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
    registrar.setUseIsoFormat(true);
    registrar.registerFormatters(dfcs);

    return dfcs;
  }

  /**
   * Test Helper to manipulate final field
   *
   * @param instance
   * @param fieldName
   * @param newValue
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   */
  public static <T> void setFinalFieldValue(T instance, String fieldName, Object newValue)
      throws IllegalAccessException, NoSuchFieldException {
    // Reference: http://java-performance.info/updating-final-and-static-final-fields/
    final Field field = ReflectionUtils.findField(instance.getClass(), fieldName, null);
    field.setAccessible(true);
    field.set(instance, newValue);
  }

  /**
   * Test Helper to call private method
   *
   * @param instance
   * @param methodToCall
   * @param parameters list of objects which will be sued as method parameters
   * @param <T> targetClass's type
   * @param <R> return type
   */
  public static<T, R> R callPrivateMethod(T instance, String methodToCall, Object... parameters) {
    @SuppressWarnings("rawtypes")
    final Class[] parameterTypes = new Class[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      parameterTypes[i] = parameters[i].getClass();
    }

    final Method method = ReflectionUtils.findMethod(instance.getClass(), methodToCall, parameterTypes);
    method.setAccessible(true);

    return (R) ReflectionUtils.invokeMethod(method, instance, parameters);
  }

  private TestUtils() {
  }
}
