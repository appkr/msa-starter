package {{packageName}}.service.mapper;

import {{packageName}}.api.model.SongDto;
import {{packageName}}.domain.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
    DateTimeMapper.class,
    AlbumMapper.class
})
public interface SongMapper extends EntityMapper<SongDto, Song> {

  @Override
  @Mapping(source = "id", target = "songId")
  SongDto toDto(Song entity);

  @Override
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  Song toEntity(SongDto dto);
}
