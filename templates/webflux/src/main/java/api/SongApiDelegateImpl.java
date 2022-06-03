package {{packageName}}.api;

import {{packageName}}.api.model.SongDto;
import {{packageName}}.api.model.SongListDto;
import {{packageName}}.service.SongService;
import {{packageName}}.support.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SongApiDelegateImpl implements SongApiDelegate {

  private final SongService service;

  @Override
  public Mono<ResponseEntity<SongDto>> createSong(Mono<SongDto> songDtoMono, ServerWebExchange exchange) {
    return service.createSong(songDtoMono)
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<ResponseEntity<SongListDto>> listSongs(Integer page, Integer size, ServerWebExchange exchange) {
    final Pageable pageable = PaginationUtils.getPageable(page, size);

    return service.listSongs(pageable)
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<ResponseEntity<SongDto>> updateSong(Long songId, Mono<SongDto> songDtoMono, ServerWebExchange exchange) {
    return service.updateSong(songId, songDtoMono)
        .map(ResponseEntity::ok);
  }

  @Override
  public Mono<ResponseEntity<Void>> deleteSong(Long songId, ServerWebExchange exchange) {
    return service.deleteSong(songId)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }
}
