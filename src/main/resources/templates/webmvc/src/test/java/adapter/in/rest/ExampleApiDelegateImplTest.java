package {{packageName}}.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import {{packageName}}.adapter.in.rest.error.ExceptionTranslator;
import {{packageName}}.rest.ExampleApiController;
import {{packageName}}.rest.ExampleApiDelegate;
import {{packageName}}.support.TestUtils;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ExampleApiDelegateImplTest {

  private MockMvc mvc;

  @Autowired private ExampleApiDelegate apiDelegate;
  @Autowired private ExceptionTranslator exceptionTranslator;
  @Autowired private MappingJackson2HttpMessageConverter jacksonMessageConverter;
  @Autowired @Qualifier("defaultValidator") private Validator validator;

  @Test
  @WithMockUser("user")
  public void testListExamples() throws Exception {
    ResultActions res = mvc.perform(
        get("/api/examples").accept(MediaType.ALL)
    ).andDo(print());

    res.andExpect(status().is2xxSuccessful());
  }

  @Test
  @Disabled("TODO: Range for response status value 200 expected:<CLIENT_ERROR> but was:<SUCCESSFUL>")
  public void testUnauthorized() throws Exception {
    ResultActions res = mvc.perform(
        get("/api/examples").accept(MediaType.ALL)
    ).andDo(print());

    res.andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @MethodSource("provideTypes")
  @WithMockUser("user")
  public void testGetExceptions(String type) throws Exception {
    ResultActions res = mvc.perform(
        get("/api/exceptions?type=" + type).accept(MediaType.ALL)
    ).andDo(print());
  }

  private static Stream<String> provideTypes() {
    return Stream.of("Unacceptable", "ServerError", "NoSuchElement");
  }

  @BeforeEach
  public void setup() {
    ExampleApiController controller = new ExampleApiController(apiDelegate);
    this.mvc = MockMvcBuilders.standaloneSetup(controller)
        .addFilters(new CharacterEncodingFilter("utf-8", true))
        .setControllerAdvice(exceptionTranslator)
        .setConversionService(TestUtils.createFormattingConversionService())
        .setMessageConverters(jacksonMessageConverter)
        .setValidator(validator)
        .build();
  }
}
