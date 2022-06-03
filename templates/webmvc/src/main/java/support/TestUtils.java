package {{packageName}}.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;

/**
 * Utility class for testing REST controllers.
 */
public final class TestUtils {

  private static final ObjectMapper mapper = createObjectMapper();

  /** MediaType for JSON UTF8 */
  public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON_UTF8;

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mapper.registerModule(new JavaTimeModule());
    // Reference for JsonNullable
    // @see https://github.com/OpenAPITools/jackson-databind-nullable#usage
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
    DefaultFormattingConversionService dfcs = new DefaultFormattingConversionService ();
    DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
    registrar.setUseIsoFormat(true);
    registrar.registerFormatters(dfcs);
    return dfcs;
  }

  /**
   * Makes a an executes a query to the EntityManager finding all stored objects.
   * @param <T> The type of objects to be searched
   * @param em The instance of the EntityManager
   * @param clss The class type to be searched
   * @return A list of all found objects
   */
  public static <T> List<T> findAll(EntityManager em, Class<T> clss) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> cq = cb.createQuery(clss);
    Root<T> rootEntry = cq.from(clss);
    CriteriaQuery<T> all = cq.select(rootEntry);
    TypedQuery<T> allQuery = em.createQuery(all);
    return allQuery.getResultList();
  }

  /**
   * Test Helper to manipulate static final field
   *
   * @param target
   * @param fieldName
   * @param newValue
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   *
   * @see http://java-performance.info/updating-final-and-static-final-fields/
   */
  public static void setStaticFinalFieldValue(Class target, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
    Field field = target.getDeclaredField(fieldName);
    field.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    field.set(null, newValue);
  }

  private TestUtils() {}
}
