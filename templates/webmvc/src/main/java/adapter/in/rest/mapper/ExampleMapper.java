package {{packageName}}.adapter.in.rest.mapper;

import {{packageName}}.domain.Example;
import {{packageName}}.rest.ExampleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface ExampleMapper extends DtoMapper<ExampleDto, Example> {

  @Override
  @Mapping(source = "id", target = "exampleId")
  ExampleDto toDto(Example entity);
}
