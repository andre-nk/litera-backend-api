package id.co.workers.Litera.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthorEntity {

  private String name;
  private String email;
}
