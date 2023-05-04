package {{packageName}}.application.port.out.spec;

import {{packageName}}.application.port.in.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
