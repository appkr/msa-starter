package {{packageName}}.application.port.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.query.UpdateMapper;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class EntityManager {

  public static final String ENTITY_ALIAS = "e";
  public static final String ALIAS_PREFIX = "e_";

  private final SqlRenderer sqlRenderer;
  private final UpdateMapper updateMapper;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  public EntityManager(SqlRenderer sqlRenderer, UpdateMapper updateMapper, R2dbcEntityTemplate r2dbcEntityTemplate) {
    this.sqlRenderer = sqlRenderer;
    this.updateMapper = updateMapper;
    this.r2dbcEntityTemplate = r2dbcEntityTemplate;
  }

  /**
   * Creates an SQL select statement from the given fragment and pagination parameters.
   * @param selectFrom a representation of a select statement.
   * @param entityType the entity type which holds the table name.
   * @param pageable page parameter, or null, if everything needs to be returned
   * @return sql select statement
   */
  public String createSelect(SelectBuilder.SelectFromAndJoin selectFrom, Class<?> entityType, Pageable pageable, Criteria criteria) {
    if (pageable != null) {
      if (criteria != null) {
        return createSelectImpl(
            selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()).where(Conditions.just(criteria.toString())),
            entityType,
            pageable.getSort()
        );
      } else {
        return createSelectImpl(
            selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()),
            entityType,
            pageable.getSort()
        );
      }
    } else {
      if (criteria != null) {
        return createSelectImpl(selectFrom.where(Conditions.just(criteria.toString())), entityType, null);
      } else {
        return createSelectImpl(selectFrom, entityType, null);
      }
    }
  }

  /**
   * Creates an SQL select statement from the given fragment and pagination parameters.
   * @param selectFrom a representation of a select statement.
   * @param entityType the entity type which holds the table name.
   * @param pageable page parameter, or null, if everything needs to be returned
   * @return sql select statement
   */
  public String createSelect(SelectBuilder.SelectFromAndJoinCondition selectFrom, Class<?> entityType, Pageable pageable, Criteria criteria) {
    if (pageable != null) {
      if (criteria != null) {
        return createSelectImpl(
            selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()).where(Conditions.just(criteria.toString())),
            entityType,
            pageable.getSort()
        );
      } else {
        return createSelectImpl(
            selectFrom.limitOffset(pageable.getPageSize(), pageable.getOffset()),
            entityType,
            pageable.getSort()
        );
      }
    } else {
      if (criteria != null) {
        return createSelectImpl(selectFrom.where(Conditions.just(criteria.toString())), entityType, null);
      } else {
        return createSelectImpl(selectFrom, entityType, null);
      }
    }
  }

  private String createSelectImpl(SelectBuilder.SelectOrdered selectFrom, Class<?> entityType, Sort sortParameter) {
    if (sortParameter != null && sortParameter.isSorted()) {
      RelationalPersistentEntity<?> entity = getPersistentEntity(entityType);
      if (entity != null) {
        Sort sort = updateMapper.getMappedObject(sortParameter, entity);
        selectFrom =
            selectFrom.orderBy(createOrderByFields(Table.create(entity.getTableName()).as(EntityManager.ENTITY_ALIAS), sort));
      }
    }
    return createSelect(selectFrom.build());
  }

  private RelationalPersistentEntity<?> getPersistentEntity(Class<?> entityType) {
    return r2dbcEntityTemplate.getConverter().getMappingContext().getPersistentEntity(entityType);
  }

  /**
   * Generate an actual SQL from the given {@link Select}.
   * @param select a representation of a select statement.
   * @return the generated SQL select.
   */
  public String createSelect(Select select) {
    return sqlRenderer.render(select);
  }

  private static Collection<? extends OrderByField> createOrderByFields(Table table, Sort sortToUse) {
    List<OrderByField> fields = new ArrayList<>();

    for (Sort.Order order : sortToUse) {
      String propertyName = order.getProperty();
      OrderByField orderByField = OrderByField.from(table.column(propertyName).as(EntityManager.ALIAS_PREFIX + propertyName));

      fields.add(order.isAscending() ? orderByField.asc() : orderByField.desc());
    }

    return fields;
  }
}

