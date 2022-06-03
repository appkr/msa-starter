package {{packageName}}.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

  private String key;
  private Operator operator;
  private Object value;

  public enum Operator {
    IN,
        GTE,
        GT,
        LTE,
        LT,
        LIKE,
        EQ
  }
}
