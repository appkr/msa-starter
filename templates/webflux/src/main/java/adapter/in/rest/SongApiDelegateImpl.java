package {{packageName}}.adapter.in.rest;

import {{packageName}}.adapter.in.rest.mapper.SongMapper;
import {{packageName}}.application.SongService;
import {{packageName}}.rest.PageDto;
import {{packageName}}.rest.SongApiDelegate;
import {{packageName}}.rest.SongDto;
import {{packageName}}.rest.SongListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SongApiDelegateImpl implements SongApiDelegate {

  private final SongService service;
  private final SongMapper mapper;

  @Override
  public Mono<ResponseEntity<SongDto>> createSong(Mono<SongDto> songDtoMono, ServerWebExchange exchange) {
    return service.createSong(songDtoMono)
        .map(entity -> ResponseEntity.ok(mapper.toDto(entity)));
  }

  @Override
  public Mono<ResponseEntity<SongListDto>> listSongs(Integer page, Integer size, ServerWebExchange exchange) {
    final Pageable pageable = PageRequest.of(page - 1, size);

    return service.listSongs(pageable)
        .collectList()
        .map(list -> {
          // TODO fix paging; query totalElements separately and zip with the list
          final SongListDto dto = new SongListDto().data(mapper.toDto(list)).page(new PageDto());
          return ResponseEntity.ok(dto);
        });
  }

  @Override
  public Mono<ResponseEntity<SongDto>> updateSong(Long songId, Mono<SongDto> songDtoMono, ServerWebExchange exchange) {
    return service.updateSong(songId, songDtoMono)
        .map(entity -> ResponseEntity.ok(mapper.toDto(entity)));
  }

  @Override
  public Mono<ResponseEntity<Void>> deleteSong(Long songId, ServerWebExchange exchange) {
    return service.deleteSong(songId)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }
}
