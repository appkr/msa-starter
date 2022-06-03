package {{packageName}}.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import {{packageName}}.IntegrationTest;
import {{packageName}}.WithMockJwt;
import {{packageName}}.domain.Album;
import {{packageName}}.domain.Song;
import {{packageName}}.support.PaginationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import reactor.test.StepVerifier;

@IntegrationTest
@WithMockJwt
class SongRepositoryTest {

  @Autowired SongRepository songRepository;
  @Autowired AlbumRepository albumRepository;

  Album album;
  Song song1;
  Song song2;

  @Test
  void findById() {
    songRepository.findById(song1.getId())
        .log()
        .as(StepVerifier::create)
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
  }

  @Test
  void findAll() {
    songRepository.findAll()
        .log()
        .as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void findAllByPageable() {
    final Pageable pageable = PaginationUtils.getPageable(1, 10);
    songRepository.findAllBy(pageable)
        .log()
        .as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void findAllByCriteria() {
    final Criteria criteria = Criteria.where(EntityManager.ENTITY_ALIAS + ".id").is(song1.getId());

    songRepository.findAllBy(null, criteria)
        .log()
        .as(StepVerifier::create)
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
  }

  @Test
  void findAllCreatedToday() {
    songRepository.findAllCreatedToday()
        .log()
        .as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void update() {
    songRepository.findById(song1.getId())
        .flatMap(entity -> {
          entity.setTitle("changed title");
          return songRepository.save(entity);
        })
        .log()
        .as(StepVerifier::create)
        .assertNext(entity -> assertEquals("changed title", entity.getTitle()))
        .verifyComplete();
  }

  @Test
  void delete() {
    songRepository.deleteById(song1.getId())
        .then(songRepository.findById(song1.getId()))
        .log()
        .as(StepVerifier::create)
        .expectNextCount(0)
        .verifyComplete();
  }

  @BeforeEach
  void setup() {
    final Album album = new Album();
    album.setTitle("이문세 5집");
    this.album = albumRepository.save(album).block();

    final Song song1 = new Song();
    song1.setTitle("시를 위한 시");
    song1.setAlbum(album);
    this.song1 = songRepository.save(song1).block();

    final Song song2 = new Song();
    song2.setTitle("광화문 연가");
    song2.setAlbum(album);
    this.song2 = songRepository.save(song2).block();
  }

  @AfterEach
  void teardown() {
    songRepository.deleteAll().block();
    albumRepository.deleteAll().block();
  }
}