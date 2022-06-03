package {{packageName}};

import static {{packageName}}.config.Constants.UNKNOWN_USER_ID;

import {{packageName}}.api.model.AlbumDto;
import {{packageName}}.api.model.SongDto;
import {{packageName}}.domain.Album;
import {{packageName}}.domain.Song;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Fixtures {

  public static AlbumDto albumDto() {
    return new AlbumDto()
        .albumId(1L)
        .title("이문세 5집")
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .createdBy(UUID.fromString(UNKNOWN_USER_ID))
        .updatedBy(UUID.fromString(UNKNOWN_USER_ID));
  }

  public static SongDto songDto() {
    return new SongDto()
        .songId(1L)
        .title("시를 위한 시")
        .album(albumDto())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .createdBy(UUID.fromString(UNKNOWN_USER_ID))
        .updatedBy(UUID.fromString(UNKNOWN_USER_ID));
  }

  public static Album album() {
    final Album entity = new Album();
    entity.setId(1L);
    entity.setTitle("이문세 5집");
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setCreatedBy(UUID.fromString(UNKNOWN_USER_ID));
    entity.setUpdatedBy(UUID.fromString(UNKNOWN_USER_ID));

    return entity;
  }

  public static Song song() {
    final Song entity = new Song();
    entity.setId(1L);
    entity.setTitle("시를 위한 시");
    entity.setAlbum(album());
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setCreatedBy(UUID.fromString(UNKNOWN_USER_ID));
    entity.setUpdatedBy(UUID.fromString(UNKNOWN_USER_ID));

    return entity;
  }
}
