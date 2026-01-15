package matteo.jwtproject.spring_jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskDTO {
    private Long id;
    private String title;
    private boolean completed;
    private String username; // opzionale: nome dell'utente proprietario
}
