package {{packageName}}.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table("songs")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Song implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  private String title;

  @Transient
  @Setter(AccessLevel.NONE)
  private Album album;
  private Long albumId;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @CreatedBy
  private UUID createdBy;

  @LastModifiedBy
  private UUID updatedBy;

  public void setAlbum(Album album) {
    this.album = album;
    if (album != null) {
      this.albumId = album.getId();
      album.addSong(this);
    }
  }
}
