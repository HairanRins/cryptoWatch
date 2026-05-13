import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../../shared/services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        localStorage.removeItem('cryptowatch_token');
        localStorage.removeItem('cryptowatch_user');
        router.navigate(['/login']);
        toast.show('Session expired. Please log in again.', 'warning');
      } else if (err.status === 0) {
        toast.show('Network error. Please check your connection.', 'error');
      } else if (err.status >= 500) {
        toast.show('Server error. Please try again later.', 'error');
      } else if (err.status >= 400 && !req.url.includes('/auth/')) {
        const message = err.error?.message || 'An unexpected error occurred.';
        toast.show(message, 'error');
      }

      return throwError(() => err);
    }),
  );
};
