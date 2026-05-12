import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  private readonly tokenKey = 'cryptowatch_token';
  private readonly userKey = 'cryptowatch_user';

  private readonly userSignal = signal<User | null>(this.getStoredUser());
  readonly user = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.userSignal() !== null);

  constructor() {
    const token = this.getToken();
    if (token) {
      this.me().pipe(catchError(() => { this.logout(); return of(null); })).subscribe();
    }
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => this.handleAuthResponse(response)),
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((response) => this.handleAuthResponse(response)),
    );
  }

  me(): Observable<User | null> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`).pipe(
      map((response) => {
        const user = response.data?.user ?? null;
        this.userSignal.set(user);
        this.storeUser(user);
        return user;
      }),
    );
  }

  logout(): void {
    this.clearStorage();
    this.userSignal.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private handleAuthResponse(response: AuthResponse): void {
    if (response.success && response.data) {
      localStorage.setItem(this.tokenKey, response.data.token);
      this.storeUser(response.data.user);
      this.userSignal.set(response.data.user);
    }
  }

  private storeUser(user: User | null): void {
    if (user) {
      localStorage.setItem(this.userKey, JSON.stringify(user));
    } else {
      localStorage.removeItem(this.userKey);
    }
  }

  private getStoredUser(): User | null {
    const stored = localStorage.getItem(this.userKey);
    if (!stored) return null;
    try { return JSON.parse(stored); } catch { return null; }
  }

  private clearStorage(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }
}
