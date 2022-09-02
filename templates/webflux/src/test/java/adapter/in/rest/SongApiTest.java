package {{packageName}}.adapter.in.rest;

import static {{packageName}}.config.Constants.V1_MEDIA_TYPE;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import {{packageName}}.Fixtures;
import {{packageName}}.IntegrationTest;
import {{packageName}}.WithMockJwt;
import {{packageName}}.application.SongService;
import {{packageName}}.rest.SongDto;
import {{packageName}}.support.JsonUtils;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@IntegrationTest
@WithMockJwt
class SongApiTest {

  @Autowired WebTestClient webTestClient;
  @MockBean SongService mockSongService;

  @Test
  void createSong() throws IOException {
    when(mockSongService.createSong(any(Mono.class)))
        .thenReturn(Mono.just(Fixtures.song()));

    webTestClient
        .post()
        .uri("/api/songs")
        .contentType(MediaType.valueOf(V1_MEDIA_TYPE))
        .bodyValue(JsonUtils.convertObjectToJsonBytes(Fixtures.songDto()))
        .exchange()
        .expectStatus().is2xxSuccessful();
  }

  @Test
  void listSongs() throws IOException {
    when(mockSongService.listSongs(any(Pageable.class)))
        .thenReturn(Flux.fromIterable(asList(Fixtures.song())));

    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/api/songs")
            .queryParam("page", 1)
            .queryParam("size", 1)
            .build()
        )
        .accept(MediaType.valueOf(V1_MEDIA_TYPE))
        .exchange()
        .expectStatus()
        .is2xxSuccessful();
  }

  @Test
  void updateSong() throws IOException {
    when(mockSongService.updateSong(anyLong(), any(Mono.class)))
        .thenReturn(Mono.just(Fixtures.song()));

    final SongDto songDto = Fixtures.songDto();
    webTestClient
        .put()
        .uri("/api/songs/{songId}", songDto.getSongId())
        .contentType(MediaType.valueOf(V1_MEDIA_TYPE))
        .bodyValue(JsonUtils.convertObjectToJsonBytes(songDto))
        .exchange()
        .expectStatus().is2xxSuccessful();
  }

  @Test
  void deleteSong() {
    when(mockSongService.deleteSong(anyLong()))
        .thenReturn(Mono.empty());

    webTestClient
        .delete()
        .uri("/api/songs/{songId}", Fixtures.songDto().getSongId())
        .exchange()
        .expectStatus().is2xxSuccessful();
  }
}