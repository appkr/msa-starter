package {{packageName}}.service.mapper;

import {{packageName}}.api.model.ExampleDto;
import {{packageName}}.domain.Example;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExampleMapperTest {

  @Autowired
  private ExampleMapper mapper;

  @Test
  public void testMapItem() {
    // given
    Example entity = Example.newInstance("original title");

    // when
    final ExampleDto dto = mapper.toDto(entity);

    // then
    assertNotNull(dto);
    assertEquals("original title", dto.getTitle());
  }

  @Test
  public void testMapList() {
    // given
    final List<Example> entityList = new ArrayList<>(
        asList(Example.newInstance("first title"), Example.newInstance("second title")));

    // when
    final List<ExampleDto> dtoList = mapper.toDto(entityList);

    // then
    assertNotNull(dtoList);
    assertTrue(dtoList.size() > 0);
  }
}