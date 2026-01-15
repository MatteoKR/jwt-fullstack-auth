package matteo.jwtproject.spring_jwt.dto;

import matteo.jwtproject.spring_jwt.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
}
