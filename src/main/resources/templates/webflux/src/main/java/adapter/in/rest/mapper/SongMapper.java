package {{packageName}}.adapter.in.rest.mapper;

import {{packageName}}.domain.Song;
import {{packageName}}.rest.SongDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
    DateTimeMapper.class,
    AlbumMapper.class
})
public interface SongMapper extends DtoMapper<SongDto, Song> {

  @Override
  @Mapping(source = "id", target = "songId")
  SongDto toDto(Song entity);
}
