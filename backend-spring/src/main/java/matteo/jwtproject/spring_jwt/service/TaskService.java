package matteo.jwtproject.spring_jwt.service;

import matteo.jwtproject.spring_jwt.model.Task;
import matteo.jwtproject.spring_jwt.model.User;
import matteo.jwtproject.spring_jwt.repository.TaskRepository;
import matteo.jwtproject.spring_jwt.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Restituisce tutti i task dell'utente loggato
    public List<Task> getTasksForUser(Authentication auth) {
        return taskRepository.findByUserUsername(auth.getName());
    }

    // Aggiunge un task associandolo all'utente loggato
    public Task addTask(Authentication auth, Task task) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        task.setUser(user);
        return taskRepository.save(task);
    }

    // Aggiorna un task solo se appartiene all'utente
    public Task updateTask(Authentication auth, Long taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task non trovato"));

        if (!task.getUser().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non puoi modificare questo task");
        }

        task.setTitle(updatedTask.getTitle());
        task.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(task);
    }

    // Cancella un task solo se appartiene all'utente
    public void deleteTask(Authentication auth, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task non trovato"));

        if (!task.getUser().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non puoi cancellare questo task");
        }

        taskRepository.delete(task);
    }
}
