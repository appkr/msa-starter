package {{packageName}}.application.port.out.persistence;

import {{packageName}}.domain.Album;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
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
public class AlbumRepositoryImpl extends SimpleR2dbcRepository<Album, Long> implements AlbumRepositoryCustom {

  private final DatabaseClient db;
  private final EntityManager entityManager;
  private final AlbumRowMapper rowMapper;
  private final SongRepository songRepository;

  private static final Table entityTable = Table.aliased("albums", EntityManager.ENTITY_ALIAS);

  public AlbumRepositoryImpl(
      R2dbcEntityTemplate template,
      EntityManager entityManager,
      AlbumRowMapper rowMapper,
      R2dbcEntityOperations entityOperations,
      R2dbcConverter converter,
      SongRepository songRepository) {
    super(
        new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Album.class)),
        entityOperations,
        converter
    );
    this.db = template.getDatabaseClient();
    this.entityManager = entityManager;
    this.rowMapper = rowMapper;
    this.songRepository = songRepository;
  }

  @Override
  public Mono<Album> findById(Long id) {
    final Criteria criteria = Criteria.where(EntityManager.ENTITY_ALIAS + ".id").is(id);
    return createQuery(null, criteria)
        .one()
        .flatMap(this::mapRelations);
  }

  @Override
  public Flux<Album> findAll() {
    return findAllBy(null, null);
  }

  @Override
  public Flux<Album> findAllBy(Pageable pageable) {
    return findAllBy(pageable, null);
  }

  @Override
  public Flux<Album> findAllBy(Pageable pageable, Criteria criteria) {
    return createQuery(pageable, criteria).all();
  }

  private RowsFetchSpec<Album> createQuery(Pageable pageable, Criteria criteria) {
    List<Expression> columns = AlbumSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
    SelectBuilder.SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
    String select = entityManager.createSelect(selectFrom, Album.class, pageable, criteria);

    return db.sql(select)
        .map((row, metadata) -> rowMapper.apply(row, "e"));
  }

  private Album process(Row row, RowMetadata metadata) {
    return rowMapper.apply(row, "e");
  }

  private Mono<Album> mapRelations(Album album) {
    final Criteria songCriteria = Criteria.where(EntityManager.ENTITY_ALIAS + ".album_id").is(album.getId());
    return songRepository.findAllBy(null, songCriteria)
        .collect(Collectors.toSet())
        .map(songSet -> {
          album.setSongs(songSet);
          return album;
        });
  }
}
