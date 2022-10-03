package {{packageName}}.adapter.in.rest.mapper;

import java.util.List;
import java.util.Set;

public interface DtoMapper<D, E> {

  D toDto(E entity);
  List <D> toDto(List<E> entityList);
  List <D> toDto(Set<E> entityList);
}
