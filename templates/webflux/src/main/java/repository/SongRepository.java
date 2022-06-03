package {{packageName}}.repository;

import {{packageName}}.domain.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongRepository extends ReactiveCrudRepository<Song, Long>, SongRepositoryCustom {

  @Override Mono<Song> findById(Long id);
  @Override Flux<Song> findAll();
  @Override Flux<Song> findAllBy(Pageable pageable);
  @Override Flux<Song> findAllBy(Pageable pageable, Criteria criteria);
}
