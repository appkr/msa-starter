package {{packageName}}.service.mapper;

import {{packageName}}.api.model.AlbumDto;
import {{packageName}}.domain.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
    DateTimeMapper.class
})
public interface AlbumMapper extends EntityMapper<AlbumDto, Album> {

  @Override
  @Mapping(source = "id", target = "albumId")
  AlbumDto toDto(Album entity);

  @Override
  @Mapping(source = "albumId", target = "id")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  Album toEntity(AlbumDto dto);
}
