import { Component, inject } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [RouterModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    remember: [false],
  });

  isLoading = false;
  error: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) return;

    this.isLoading = true;
    this.error = null;

    this.auth.login({
      email: this.form.value.email!,
      password: this.form.value.password!,
    }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        this.error = err.error?.message || 'Login failed. Please try again.';
        this.isLoading = false;
      },
    });
  }
}
