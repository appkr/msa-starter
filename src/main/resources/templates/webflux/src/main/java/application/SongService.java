package {{packageName}}.application;

import {{packageName}}.application.port.out.persistence.AlbumRepository;
import {{packageName}}.application.port.out.persistence.SongRepository;
import {{packageName}}.domain.Album;
import {{packageName}}.domain.Song;
import {{packageName}}.rest.SongDto;
import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SongService {

  private final SongRepository repository;
  private final AlbumRepository albumRepository;

  @Transactional
  public Mono<Song> createSong(Mono<SongDto> songDtoMono) {
    return songDtoMono
        .flatMap(dto -> {
          final Song entity = new Song();
          entity.setTitle(dto.getTitle());

          return albumRepository.findById(dto.getAlbum().getAlbumId())
              .switchIfEmpty(Mono.just(new Album()))
              .map(album -> {
                entity.setAlbum(album);
                return entity;
              });
        })
        .flatMap(repository::save);
  }

  @Transactional(readOnly = true)
  public Flux<Song> listSongs(Pageable pageable) {
    return repository.findAllBy(pageable);
  }

  @Transactional
  public Mono<Song> updateSong(Long songId, Mono<SongDto> songDtoMono) {
    return repository.findById(songId)
        .switchIfEmpty(Mono.error(new NoSuchElementException()))
        .zipWith(songDtoMono, (entity, dto) -> {
          entity.setTitle(dto.getTitle());
          return repository.save(entity);
        })
        .flatMap(Function.identity());
  }

  @Transactional
  public Mono<Void> deleteSong(Long songId) {
    return repository.deleteById(songId);
  }
}
