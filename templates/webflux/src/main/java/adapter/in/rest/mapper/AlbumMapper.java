package {{packageName}}.adapter.in.rest.mapper;

import {{packageName}}.domain.Album;
import {{packageName}}.rest.AlbumDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
    DateTimeMapper.class
})
public interface AlbumMapper extends DtoMapper<AlbumDto, Album> {

  @Override
  @Mapping(source = "id", target = "albumId")
  AlbumDto toDto(Album entity);
}
