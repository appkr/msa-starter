package {{packageName}}.repository;

import {{packageName}}.domain.Album;
import {{packageName}}.repository.converter.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

@Service
public class AlbumRowMapper implements BiFunction<Row, String, Album> {

  private final ColumnConverter converter;

  public AlbumRowMapper(ColumnConverter converter) {
    this.converter = converter;
  }

  @Override
  public Album apply(Row row, String prefix) {
    final Album entity = new Album();
    entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
    entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
    entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
    entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
    entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", UUID.class));
    entity.setUpdatedBy(converter.fromRow(row, prefix + "_updated_by", UUID.class));

    return entity;
  }
}
