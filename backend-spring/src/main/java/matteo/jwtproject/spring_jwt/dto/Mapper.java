package matteo.jwtproject.spring_jwt.dto;

import matteo.jwtproject.spring_jwt.model.Task;
import matteo.jwtproject.spring_jwt.model.User;

public class Mapper {

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }

    public static TaskDTO toTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setCompleted(task.isCompleted());
        if (task.getUser() != null) {
            dto.setUsername(task.getUser().getUsername());
        }
        return dto;
    }
}
