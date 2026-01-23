import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router'; 
import { AuthService } from '../../auth/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterModule, NgIf],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  username = '';
  password = '';
  errorMessage = ''; // messaggio di errore

  constructor(
    private authService: AuthService,
    private router: Router // Router per navigare al login
  ) {}

  register() {
    this.errorMessage = ''; // reset prima di fare la registrazione
    console.log('Register button clicked', this.username, this.password);

    this.authService
      .register({
        username: this.username,
        password: this.password,
      })
      .subscribe({
        next: (res: string) => {
          console.log('Registrazione avvenuta:', res);
          // reindirizza al login dopo la registrazione
          alert(res);
          this.router.navigate(['/login']);
        },
        error: (err: any) => {
          console.error('Errore registrazione', err);
          this.errorMessage = err.error || 'Errore generico';
        },
      });
  }
}
