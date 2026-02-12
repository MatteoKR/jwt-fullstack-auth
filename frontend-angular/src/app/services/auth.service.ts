import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginRequest, RegisterRequest } from '../models/user.model';
import { tap } from 'rxjs/operators';

/**
 * Il backend invia il token JWT al frontend nel corpo della risposta HTTP 
 * del metodo di login, una volta verificate le credenziali dell’utente. 
 * Il frontend lo riceve e lo salva per usarlo nelle richieste successive.
 *
 * Questa classe gestisce tutte le operazioni di autenticazione lato frontend:
 * - registrazione di un nuovo utente
 * - login e salvataggio del token JWT
 * - logout e gestione dello stato di login
 * - lettura dello username dal token salvato
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // URL base delle API di autenticazione
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  // Metodo per registrare un nuovo utente
  register(data: RegisterRequest) {
    // Faccio la POST al backend con i dati di registrazione
    // responseType: 'text' perché il backend restituisce una stringa
    return this.http.post(
      `${this.apiUrl}/register`,
      data,
      { responseType: 'text' }
    );
  }

  // Metodo per fare login e ricevere il token JWT
  login(credentials: LoginRequest) {
    // Invio username e password al backend
    return this.http
      .post<{ token: string }>(`${this.apiUrl}/login`, credentials)
      // Salvo il token JWT nel localStorage una volta ricevuto
      .pipe(tap((response) => this.saveToken(response.token)));
  }

  // Salvo il token JWT nel localStorage
  saveToken(token: string) {
    localStorage.setItem('jwtToken', token);
  }

  // Recupero il token JWT salvato nel localStorage
  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  // Effettuo il logout eliminando il token dal localStorage
  logout() {
    localStorage.removeItem('jwtToken');
  }

  // Controllo se l’utente è considerato loggato
  isLoggedIn(): boolean {
    // Ritorno true se esiste un token, false altrimenti
    return !!this.getToken();
  }

  // Recupero lo username direttamente dal token JWT salvato
  getUsernameFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;

    // Decodifico la parte payload del token e leggo il campo sub (subject)
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub; // lo username
  }
}
