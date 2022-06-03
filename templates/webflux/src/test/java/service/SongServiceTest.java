package {{packageName}}.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import {{packageName}}.Fixtures;
import {{packageName}}.IntegrationTest;
import {{packageName}}.WithMockJwt;
import {{packageName}}.domain.Song;
import {{packageName}}.repository.SongRepository;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@IntegrationTest
@WithMockJwt
class SongServiceTest {

  @MockBean SongRepository mockSongRepository;
  @Autowired SongService songService;

  Song song;

  @Test
  void createSong() {
    when(mockSongRepository.save(any(Song.class)))
        .thenReturn(Mono.just(song));

    songService.createSong(Mono.just(Fixtures.songDto()))
        .log()
        .as(StepVerifier::create)
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
  }

  @Test
  void listSongs() {
    when(mockSongRepository.findAllBy(any(Pageable.class)))
        .thenReturn(Flux.just(song));

    songService.listSongs(PageRequest.of(0, 1))
        .log()
        .as(StepVerifier::create)
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
  }

  @Test
  void updateSong() {
    when(mockSongRepository.findById(anyLong()))
        .thenReturn(Mono.just(song));
    when(mockSongRepository.save(any(Song.class)))
        .thenReturn(Mono.just(song));

    songService.updateSong(song.getId(), Mono.just(Fixtures.songDto()))
        .log()
        .as(StepVerifier::create)
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
  }

  @Test
  void updateSong_expectError_whenEntityNotExist() {
    when(mockSongRepository.findById(anyLong()))
        .thenReturn(Mono.empty());

    songService.updateSong(song.getId(), Mono.just(Fixtures.songDto()))
        .log()
        .as(StepVerifier::create)
        .expectError(NoSuchElementException.class)
        .verify();
  }

  @Test
  void deleteSong() {
    when(mockSongRepository.deleteById(anyLong()))
        .thenReturn(Mono.empty());

    songService.deleteSong(song.getId())
        .log()
        .as(StepVerifier::create)
        .verifyComplete();
  }

  @BeforeEach
  void setup() {
    song = Fixtures.song();
  }
}