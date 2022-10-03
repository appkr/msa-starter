package {{packageName}};

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { Application.class, ApplicationTestConfiguration.class })
@AutoConfigureWebTestClient
public @interface IntegrationTest {
  String DEFAULT_TIMEOUT = "PT5S";
}
