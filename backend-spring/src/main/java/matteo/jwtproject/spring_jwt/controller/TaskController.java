package matteo.jwtproject.spring_jwt.controller;

import matteo.jwtproject.spring_jwt.dto.Mapper;
import matteo.jwtproject.spring_jwt.dto.TaskDTO;
import matteo.jwtproject.spring_jwt.model.Task;
import matteo.jwtproject.spring_jwt.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDTO> getMyTasks(Authentication auth) {
        return taskService.getTasksForUser(auth)
                          .stream()
                          .map(Mapper::toTaskDTO)
                          .toList();
    }

    @PostMapping
    public TaskDTO addTask(Authentication auth, @RequestBody Task task) {
        Task savedTask = taskService.addTask(auth, task);
        return Mapper.toTaskDTO(savedTask);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(Authentication auth, @PathVariable("id") Long id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(auth, id, task);
        return Mapper.toTaskDTO(updatedTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(Authentication auth, @PathVariable("id") Long id) {
        taskService.deleteTask(auth, id);
    }
}