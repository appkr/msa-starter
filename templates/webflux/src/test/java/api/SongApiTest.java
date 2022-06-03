package {{packageName}}.api;

import static {{packageName}}.config.Constants.V1_MEDIA_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import {{packageName}}.Fixtures;
import {{packageName}}.IntegrationTest;
import {{packageName}}.WithMockJwt;
import {{packageName}}.api.model.SongDto;
import {{packageName}}.api.model.SongListDto;
import {{packageName}}.service.SongService;
import {{packageName}}.support.JsonUtils;
import {{packageName}}.support.PaginationUtils;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@IntegrationTest
@WithMockJwt
class SongApiTest {

  @Autowired WebTestClient webTestClient;
  @MockBean SongService mockSongService;
  SongDto songDto;

  @Test
  void createSong() throws IOException {
    when(mockSongService.createSong(any(Mono.class)))
        .thenReturn(Mono.just(songDto));

    webTestClient
        .post()
        .uri("/api/songs")
        .contentType(MediaType.valueOf(V1_MEDIA_TYPE))
        .bodyValue(JsonUtils.convertObjectToJsonBytes(songDto))
        .exchange()
        .expectStatus().is2xxSuccessful();
  }

  @Test
  void listSongs() throws IOException {
    final SongListDto songListDto = new SongListDto()
        .data(List.of(songDto))
        .page(PaginationUtils.toPageDto(1L, PageRequest.of(0, 1)));
    when(mockSongService.listSongs(any(Pageable.class)))
        .thenReturn(Mono.just(songListDto));

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
        .thenReturn(Mono.just(songDto));

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
        .uri("/api/songs/{songId}", songDto.getSongId())
        .exchange()
        .expectStatus().is2xxSuccessful();
  }

  @BeforeEach
  void setup() {
    songDto = Fixtures.songDto();
  }
}