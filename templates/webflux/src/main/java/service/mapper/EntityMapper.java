package {{packageName}}.service.mapper;

import java.util.List;
import java.util.Set;

public interface EntityMapper <D, E> {

  D toDto(E entity);
  List <D> toDto(List<E> entityList);
  List <D> toDto(Set<E> entityList);

  E toEntity(D dto);
}
