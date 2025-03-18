import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  imagePath: string = "image/richcon-logo.png";
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.loading = true;
      this.errorMessage = '';
      const { username, password } = this.loginForm.value;
      
      this.authService.login(username, password).pipe(
        finalize(() => this.loading = false)
      ).subscribe({
        next: (response) => {
          if (response.access_token) {
            const userRole = this.authService.getCurrentUserRole();
            switch (userRole) {
              case 'ADMIN':
                this.router.navigate(['/admin-dashboard']);
                break;
              case 'ENTRY':
                this.router.navigate(['/entry-dashboard']);
                break;
              case 'MANAGER':
                this.router.navigate(['/manager-dashboard']);
                break;
              case 'OPERATION':
                this.router.navigate(['/operation-dashboard']);
                break;
              case 'DEALER':
                this.router.navigate(['/dealer-dashboard']);
                break;
              default:
                this.errorMessage = 'Unknown role. Please contact support.';
                this.router.navigate(['/login']);
                break;
            }
          } else {
            this.errorMessage = 'Invalid login response';
          }
        },
        error: (error) => {
          console.error('Login failed', error);
          this.errorMessage = error.error?.message || 'Login failed. Please check your credentials.';
        },
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}