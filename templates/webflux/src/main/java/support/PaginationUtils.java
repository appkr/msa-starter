package {{packageName}}.support;

import {{packageName}}.api.model.PageDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {

  public static Integer DEFAULT_PAGE_SIZE = 20;

  public static Pageable getPageable() {
    return getPageable(null, null);
  }

  public static Pageable getPageable(Integer page, Integer size) {
    if (page == null) {
      page = 1;
    }
    if (size == null) {
      size = DEFAULT_PAGE_SIZE;
    }
    Sort sort = Sort.by(Sort.Direction.DESC, "id");

    return PageRequest.of((page == 1) ? 0 : page - 1, size, sort);
  }

  public static PageDto toPageDto(long totalElements, Pageable pageable) {
    final int totalPages = (int) Math.ceil(totalElements / (double) pageable.getPageSize());

    return new PageDto()
        .number(pageable.getPageNumber() + 1)
        .size(pageable.getPageSize())
        .totalElements(totalElements)
        .totalPages(totalPages);
  }
}