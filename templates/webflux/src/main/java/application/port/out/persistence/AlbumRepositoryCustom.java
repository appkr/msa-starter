package {{packageName}}.application.port.out.persistence;

import {{packageName}}.domain.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumRepositoryCustom {

  Mono<Album> findById(Long id);
  Flux<Album> findAll();
  Flux<Album> findAllBy(Pageable pageable);
  Flux<Album> findAllBy(Pageable pageable, Criteria criteria);
}
