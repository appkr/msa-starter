package {{packageName}}.repository.spec;

import {{packageName}}.service.dto.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class GenericSpecification<T> implements Specification<T> {

  private final SearchCriteria criteria;

  public GenericSpecification(SearchCriteria criteria) {
    this.criteria = criteria;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    final String key = criteria.getKey();
    final String value = criteria.getValue().toString();
    switch (criteria.getOperator()) {
      case IN:
        return root.get(key).in(value);
      case GTE:
        return cb.greaterThanOrEqualTo(root.get(key), value);
      case GT:
        return cb.greaterThan(root.get(key), value);
      case LTE:
        return cb.lessThanOrEqualTo(root.get(key), value);
      case LT:
        return cb.lessThan(root.get(key), value);
      case LIKE:
        return cb.like(root.get(key), "%" + value + "%");
      default:
        return cb.equal(root.get(key), value);
    }
  }
}
