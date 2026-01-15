import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task/task.service';
import { Task } from '../models/task.model';
import { AuthService } from '../auth/auth.service';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  tasks: Task[] = [];
  newTaskTitle = '';
  errorMessage = '';
  username = '';

  constructor(private taskService: TaskService, private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.username = this.authService.getUsernameFromToken() ?? '';
    this.loadTasks();
  }

  // Carica i task dell'utente loggato dal backend
  loadTasks() {
    this.taskService.getTasks().subscribe({
      next: tasks => this.tasks = tasks,
      error: err => this.errorMessage = err.error || 'Errore nel caricamento dei task'
    });
  }

  addTask() {
    if (!this.newTaskTitle.trim()) return;

    const task: Task = { title: this.newTaskTitle, completed: false };
    this.taskService.addTask(task).subscribe({
      next: t => {
        this.tasks.push(t);
        this.newTaskTitle = '';
      },
      error: err => this.errorMessage = err.error || 'Errore durante l\'aggiunta'
    });
  }

  toggleCompletion(task: Task) {
    const updatedTask = { ...task, completed: !task.completed };
    this.taskService.updateTask(updatedTask).subscribe({
      next: t => task.completed = t.completed,  // aggiorna lo stato con quello del backend
      error: err => this.errorMessage = err.error || 'Errore durante l\'aggiornamento'
    });
  }

  deleteTask(task: Task) {
    if (!task.id) return;
    this.taskService.deleteTask(task.id).subscribe({
      next: () => this.tasks = this.tasks.filter(t => t.id !== task.id),
      error: err => this.errorMessage = err.error || 'Errore durante la cancellazione'
    });
  }

  logout() {
    localStorage.removeItem('jwtToken');
    this.router.navigate(['/login']);
  }
}
