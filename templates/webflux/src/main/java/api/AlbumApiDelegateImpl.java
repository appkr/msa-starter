package {{packageName}}.api;

import {{packageName}}.api.model.AlbumDetailDto;
import {{packageName}}.repository.AlbumRepository;
import {{packageName}}.service.mapper.DateTimeMapper;
import {{packageName}}.service.mapper.SongMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AlbumApiDelegateImpl implements AlbumApiDelegate {

  private final AlbumRepository repository;
  private final DateTimeMapper dateTimeMapper;
  private final SongMapper songMapper;

  @Override
  @Transactional(readOnly = true)
  public Mono<ResponseEntity<AlbumDetailDto>> getAlbum(Long albumId, ServerWebExchange exchange) {
    return repository.findById(albumId)
        .map(album -> {
          final AlbumDetailDto dto = new AlbumDetailDto()
              .albumId(album.getId())
              .title(album.getTitle())
              .createdAt(dateTimeMapper.toOffsetDateTime(album.getCreatedAt()))
              .createdBy(album.getCreatedBy())
              .updatedAt(dateTimeMapper.toOffsetDateTime(album.getUpdatedAt()))
              .updatedBy(album.getUpdatedBy())
              .songs(songMapper.toDto(album.getSongs()));

          return ResponseEntity.ok(dto);
        });
  }
}
