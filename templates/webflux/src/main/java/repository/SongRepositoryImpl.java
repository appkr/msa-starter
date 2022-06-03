package {{packageName}}.repository;

import {{packageName}}.domain.Album;
import {{packageName}}.domain.Song;
import {{packageName}}.support.Carbon;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class SongRepositoryImpl extends SimpleR2dbcRepository<Song, Long> implements SongRepositoryCustom {

  private final DatabaseClient db;
  private final EntityManager entityManager;
  private final SongRowMapper rowMapper;
  private final AlbumRowMapper albumRowMapper;

  private static final Table entityTable = Table.aliased("songs", EntityManager.ENTITY_ALIAS);
  private static final Table albumTable = Table.aliased("albums", "albums");

  public SongRepositoryImpl(
      R2dbcEntityTemplate template,
      EntityManager entityManager,
      SongRowMapper rowMapper,
      AlbumRowMapper albumRowMapper,
      R2dbcEntityOperations entityOperations,
      R2dbcConverter converter) {
    super(
        new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Song.class)),
        entityOperations,
        converter
    );
    this.db = template.getDatabaseClient();
    this.entityManager = entityManager;
    this.rowMapper = rowMapper;
    this.albumRowMapper = albumRowMapper;
  }

  @Override
  public Mono<Song> findById(Long id) {
    final Criteria criteria = Criteria.where(EntityManager.ENTITY_ALIAS + ".id").is(id);
    return createQuery(null, criteria).one();
  }

  @Override
  public Flux<Song> findAll() {
    return findAllBy(null, null);
  }

  @Override
  public Flux<Song> findAllBy(Pageable pageable) {
    return findAllBy(pageable, null);
  }

  @Override
  public Flux<Song> findAllBy(Pageable pageable, Criteria criteria) {
    return createQuery(pageable, criteria).all();
  }

  @Override
  public Flux<Song> findAllCreatedToday() {
    final String sql =
        "SELECT e.id AS e_id, " +
            "e.title AS e_title, " +
            "e.album_id AS e_album_id, " +
            "e.created_at AS e_created_at, " +
            "e.updated_at AS e_updated_at, " +
            "e.created_by AS e_created_by, " +
            "e.updated_by AS e_updated_by, " +
            "albums.id AS albums_id, " +
            "albums.title AS albums_title, " +
            "albums.created_at AS albums_created_at, " +
            "albums.updated_at AS albums_updated_at, " +
            "albums.created_by AS albums_created_by, " +
            "albums.updated_by AS albums_updated_by " +
        "FROM songs AS e " +
        "INNER JOIN albums AS albums ON e.album_id = albums.id " +
        "WHERE e.created_at >= :from AND e.created_at <= :to";

    return db.sql(sql)
        .bind("from", Carbon.seoul().startOfDay().toInstant())
        .bind("to", Carbon.seoul().endOfDay().toInstant())
        .map(this::process)
        .all();
  }

  private RowsFetchSpec<Song> createQuery(Pageable pageable, Criteria criteria) {
    List<Expression> columns = SongSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
    columns.addAll(AlbumSqlHelper.getColumns(albumTable, "albums"));

    SelectBuilder.SelectFromAndJoinCondition selectFrom = Select
        .builder()
        .select(columns)
        .from(entityTable)
        .join(albumTable)
        .on(Column.create("album_id", entityTable))
        .equals(Column.create("id", albumTable));

    String select = entityManager.createSelect(selectFrom, Song.class, pageable, criteria);

    return db.sql(select)
        .map(this::process);
  }

  private Song process(Row row, RowMetadata metadata) {
    final Album album = albumRowMapper.apply(row, "albums");
    Song entity = rowMapper.apply(row, "e");
    entity.setAlbum(album);

    return entity;
  }
}
