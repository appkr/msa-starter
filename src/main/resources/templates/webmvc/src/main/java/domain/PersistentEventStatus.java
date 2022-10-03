package {{packageName}}.domain;

import lombok.Getter;

@Getter
public enum PersistentEventStatus {
  CREATED(10),  // Initial state
  PRODUCED(20), // When produced via a message broker
  CONSUMED(30), // When success callback received from a consumer
  FAILED(40)    // When fail callback received from a consumer
  ;

  private Integer code;

  PersistentEventStatus(Integer code) {
    this.code = code;
  }
}
