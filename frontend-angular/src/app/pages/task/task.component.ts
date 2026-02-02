import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import { Task } from '../../models/task.model';
import { MatIconModule } from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatDividerModule} from '@angular/material/divider';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-task',
  standalone: true,
  templateUrl: './task.component.html',
  imports: [MatIconModule, MatButtonModule, MatDividerModule, FormsModule],
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  username = '';
  task: Task = { id: 0, title: '', completed: false };
  errorMessage = '';
  originalTitle = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsernameFromToken() ?? '';

    const taskId = Number(this.route.snapshot.paramMap.get('id'));
    if (taskId) {
      this.taskService.getTasks().subscribe({
        next: (tasks) => {
          const t = tasks.find(task => task.id === taskId);
          if (t) {
            this.task = t;
            this.originalTitle = t.title;
          } else {
            this.errorMessage = 'Task non trovato';
          }
        },
        error: () => this.errorMessage = 'Errore nel recupero del task'
      });
    }
  }

  goBack() {
    this.router.navigate(['/home']);
  }

  saveTask() {
    if (!this.task.title.trim()) {
      this.errorMessage = 'Il titolo del task non può essere vuoto';
      return;
    }

    this.taskService.updateTask(this.task).subscribe({
      next: () => this.goBack(),
      error: () => this.errorMessage = 'Errore durante il salvataggio del task'
    });
  }

  logout() {
    localStorage.removeItem('jwtToken');
    this.router.navigate(['/login']);
  }
}
