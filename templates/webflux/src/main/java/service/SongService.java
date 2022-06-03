package {{packageName}}.service;

import {{packageName}}.api.model.SongDto;
import {{packageName}}.api.model.SongListDto;
import {{packageName}}.repository.SongRepository;
import {{packageName}}.service.mapper.SongMapper;
import {{packageName}}.support.PaginationUtils;
import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SongService {

  private final SongRepository repository;
  private final SongMapper mapper;

  @Transactional
  public Mono<SongDto> createSong(Mono<SongDto> songDtoMono) {
    return songDtoMono
        .map(mapper::toEntity)
        .flatMap(repository::save)
        .map(mapper::toDto);
  }

  @Transactional(readOnly = true)
  public Mono<SongListDto> listSongs(Pageable pageable) {
    return repository.findAllBy(pageable)
        .map(mapper::toDto)
        .collectList()
        .map(songList -> {
          return new SongListDto()
              .data(songList)
              .page(PaginationUtils.toPageDto(songList.size(), pageable));
        });
  }

  @Transactional
  public Mono<SongDto> updateSong(Long songId, Mono<SongDto> songDtoMono) {
    return repository.findById(songId)
        .switchIfEmpty(Mono.error(new NoSuchElementException()))
        .zipWith(songDtoMono, (entity, dto) -> {
          entity.setTitle(dto.getTitle());
          return repository.save(entity);
        })
        .flatMap(Function.identity())
        .map(mapper::toDto);
  }

  @Transactional
  public Mono<Void> deleteSong(Long songId) {
    return repository.deleteById(songId);
  }
}
