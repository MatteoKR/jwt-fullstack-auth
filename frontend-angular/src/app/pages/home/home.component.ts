import { Component, OnInit } from '@angular/core';
import { Task } from '../../models/task.model';
import { TaskService } from '../../task/task.service';
import { AuthService } from '../../auth/auth.service';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  tasks: Task[] = [];
  newTaskTitle = '';
  errorMessage = '';
  username = '';
  taskToDelete: Task | null = null;
  showDeleteConfirm = false;

  constructor(
    private taskService: TaskService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.username = this.authService.getUsernameFromToken() ?? '';
    this.loadTasks();
  }

  // Carica i task dell'utente loggato dal backend
  loadTasks() {
    this.taskService.getTasks().subscribe({
      next: (tasks) => (this.tasks = tasks),
      error: (err) =>
        (this.errorMessage = err.error || 'Errore nel caricamento dei task'),
    });
  }

  addTask() {
    if (!this.newTaskTitle.trim()) return;

    const task: Task = { title: this.newTaskTitle, completed: false };
    this.taskService.addTask(task).subscribe({
      next: (t) => {
        this.tasks.push(t);
        this.newTaskTitle = '';
      },
      error: (err) =>
        (this.errorMessage = err.error || "Errore durante l'aggiunta"),
    });
  }

  toggleCompletion(task: Task) {
    const updatedTask = { ...task, completed: !task.completed };
    this.taskService.updateTask(updatedTask).subscribe({
      next: (t) => (task.completed = t.completed), // aggiorna lo stato con quello del backend
      error: (err) =>
        (this.errorMessage = err.error || "Errore durante l'aggiornamento"),
    });
  }

  editTask(task: Task) {
    this.router.navigate(['/task', task.id]);
  }

  confirmDelete(task: Task) {
    this.taskToDelete = task;
    this.showDeleteConfirm = true;
  }

  cancelDelete() {
    this.taskToDelete = null;
    this.showDeleteConfirm = false;
  }

  deleteConfirmed() {
    if (!this.taskToDelete?.id) return;

    this.taskService.deleteTask(this.taskToDelete.id).subscribe({
      next: () => {
        this.tasks = this.tasks.filter((t) => t.id !== this.taskToDelete!.id);
        this.cancelDelete();
      },
      error: (err) => {
        this.errorMessage = err.error || 'Errore durante la cancellazione';
        this.cancelDelete();
      },
    });
  }

  logout() {
    localStorage.removeItem('jwtToken');
    this.router.navigate(['/login']);
  }
}
