package {{packageName}}.service.mapper;

import {{packageName}}.api.model.ExampleDto;
import {{packageName}}.domain.Example;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface ExampleMapper extends EntityMapper<ExampleDto, Example>{

  @Override
  @Mapping(source = "id", target = "exampleId")
  ExampleDto toDto(Example entity);
}
