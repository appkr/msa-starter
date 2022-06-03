package {{packageName}}.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

@Table("albums")
@Getter
@Setter
@ToString(exclude = "songs")
@EqualsAndHashCode(of = {"id"})
public class Album implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long id;

  private String title;

  @Transient
  private Set<Song> songs = new HashSet<>();

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @CreatedBy
  private UUID createdBy;

  @LastModifiedBy
  private UUID updatedBy;

  public void addSong(Song song) {
    if (song != null) {
      this.songs.add(song);
    }
  }
}
