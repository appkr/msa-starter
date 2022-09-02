package {{packageName}}.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "examples")
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class Example implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String updatedBy;

  protected Example() {}

  private Example(String title) {
    this.title = title;
  }

  public static Example create(String title) {
    return new Example(title);
  }

  public void changeTitle(String newTitle) {
    this.title = newTitle;
  }
}
