package {{packageName}}.repository;

import {{packageName}}.domain.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongRepositoryCustom {

  Mono<Song> findById(Long id);
  Flux<Song> findAll();
  Flux<Song> findAllBy(Pageable pageable);
  Flux<Song> findAllBy(Pageable pageable, Criteria criteria);
  Flux<Song> findAllCreatedToday();
}
