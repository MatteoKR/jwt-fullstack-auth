import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Task } from '../models/task.model';
import { Observable } from 'rxjs';

/**
 * Questa classe gestisce tutte le chiamate HTTP relative alle Task.
 * Ogni richiesta invia il token JWT salvato nel localStorage come header di autorizzazione,
 * così il backend può verificare l’identità dell’utente.
 * 
 * In pratica:
 * - Recupera le task dal backend
 * - Aggiunge, modifica ed elimina task
 * - Gestisce automaticamente l’header Authorization con il token JWT
 */
@Injectable({
  providedIn: 'root'
})
export class TaskService {
  // URL base delle API delle task
  private apiUrl = 'http://localhost:8080/tasks';

  constructor(private http: HttpClient) {}

  // Preparo gli header Authorization per tutte le chiamate protette
  private getAuthHeaders(): { headers: HttpHeaders } {
    // Recupero il token JWT salvato nel localStorage
    const token = localStorage.getItem('jwtToken');

    // Ritorno gli header con il Bearer token
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  // Recupero tutte le task dal backend
  getTasks(): Observable<Task[]> {
    // Faccio una GET passando gli header con il token
    return this.http.get<Task[]>(this.apiUrl, this.getAuthHeaders());
  }

  // Aggiungo una nuova task sul backend
  addTask(task: Task): Observable<Task> {
    // Faccio una POST inviando l’oggetto task e gli header di autorizzazione
    return this.http.post<Task>(this.apiUrl, task, this.getAuthHeaders());
  }

  // Aggiorno una task esistente
  updateTask(task: Task): Observable<Task> {
    // Faccio una PUT indicando l’id della task e passando gli header con il token
    return this.http.put<Task>(`${this.apiUrl}/${task.id}`, task, this.getAuthHeaders());
  }

  // Elimino una task
  deleteTask(taskId: number): Observable<void> {
    // Faccio una DELETE passando l’id e gli header di autorizzazione
    return this.http.delete<void>(`${this.apiUrl}/${taskId}`, this.getAuthHeaders());
  }
}
