package {{packageName}}.support;

import {{packageName}}.api.model.PageDto;
import org.springframework.data.domain.Page;
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

  public static PageDto getPageDto(Page<?> page) {
    return getPageDto(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
  }

  public static PageDto getPageDto(int number, int size, long totalElements, int totalPages) {
    return new PageDto()
        .number(number + 1)
        .size(size)
        .totalElements(totalElements)
        .totalPages(totalPages);
  }
}