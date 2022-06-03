package {{packageName}}.repository;

import {{packageName}}.domain.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumRepository extends ReactiveCrudRepository<Album, Long>, AlbumRepositoryCustom {

  @Override Mono<Album> findById(Long id);
  @Override Flux<Album> findAll();
  @Override Flux<Album> findAllBy(Pageable pageable);
  @Override Flux<Album> findAllBy(Pageable pageable, Criteria criteria);
}
