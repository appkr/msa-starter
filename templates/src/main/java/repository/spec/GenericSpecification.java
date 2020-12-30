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
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    if (criteria.getOperation().equalsIgnoreCase("in")) {
      return root.get(criteria.getKey()).in(criteria.getValue());
    } else if (criteria.getOperation().equalsIgnoreCase(">=")) {
      return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
    } else if (criteria.getOperation().equalsIgnoreCase(">")) {
      return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
    } else if (criteria.getOperation().equalsIgnoreCase("<=")) {
      return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
    } else if (criteria.getOperation().equalsIgnoreCase("<")) {
      return builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
    } else if (criteria.getOperation().equalsIgnoreCase("like")) {
      return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
    } else {
      return builder.equal(root.get(criteria.getKey()), criteria.getValue());
    }
  }
}