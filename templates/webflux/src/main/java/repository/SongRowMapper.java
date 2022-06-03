package {{packageName}}.repository;

import {{packageName}}.domain.Song;
import {{packageName}}.repository.converter.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

@Service
public class SongRowMapper implements BiFunction<Row, String, Song> {

  private final ColumnConverter converter;

  public SongRowMapper(ColumnConverter converter) {
    this.converter = converter;
  }

  @Override
  public Song apply(Row row, String prefix) {
    final Song entity = new Song();
    entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
    entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
    entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
    entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
    entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", UUID.class));
    entity.setUpdatedBy(converter.fromRow(row, prefix + "_updated_by", UUID.class));

    entity.setAlbumId(converter.fromRow(row, prefix + "_album_id", Long.class));

    return entity;
  }
}
