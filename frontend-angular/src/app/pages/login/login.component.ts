import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { NgIf } from '@angular/common'; 
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule,NgIf], // <-- aggiungi qui
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = ''; // <-- messaggio di errore
  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.errorMessage = ''; // reset prima di fare login
    console.log('Login button clicked', this.username, this.password);
    this.authService
      .login({ username: this.username, password: this.password })
      .subscribe({
        next: (token) => {
          console.log('Token ricevuto:', token);
          //alert('Login avvenuto!');
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Errore login', err);
          this.errorMessage = err.error || 'Password errata';
        },
      });
  }
}
