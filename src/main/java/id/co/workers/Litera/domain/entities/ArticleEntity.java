package id.co.workers.Litera.domain.entities;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ArticleEntity {

  private String id;
  private AuthorEntity author;
  private String title;
  private String[] content;
  private LocalDateTime sentAt;
}
