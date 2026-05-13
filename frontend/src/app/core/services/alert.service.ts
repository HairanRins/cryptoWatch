import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../shared/services/toast.service';

export interface Alert {
  id: string;
  portfolioId: string;
  coinId: string;
  symbol: string;
  condition: 'ABOVE' | 'BELOW';
  targetPrice: number;
  channels: string[];
  recurring: boolean;
  enabled: boolean;
  created: string;
  lastChecked: string;
}

export interface CreateAlertRequest {
  coinId: string;
  condition: 'ABOVE' | 'BELOW';
  targetPrice: number;
  channels: string[];
  recurring: boolean;
}

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly http = inject(HttpClient);
  private readonly toast = inject(ToastService);
  private readonly apiUrl = environment.apiUrl;

  getAlerts(portfolioId: string): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.apiUrl}/portfolios/${portfolioId}/alerts`).pipe(
      catchError(() => {
        this.toast.show('Could not load alerts.', 'warning');
        return of([]);
      }),
    );
  }

  createAlert(portfolioId: string, request: CreateAlertRequest): Observable<Alert | null> {
    return this.http.post<Alert>(`${this.apiUrl}/portfolios/${portfolioId}/alerts`, request).pipe(
      catchError(() => {
        this.toast.show('Could not create alert.', 'error');
        return of(null);
      }),
    );
  }

  toggleAlert(portfolioId: string, alertId: string, enabled: boolean): Observable<Alert | null> {
    return this.http.patch<Alert>(`${this.apiUrl}/portfolios/${portfolioId}/alerts/${alertId}`, { enabled }).pipe(
      catchError(() => {
        this.toast.show('Could not update alert.', 'error');
        return of(null);
      }),
    );
  }

  deleteAlert(portfolioId: string, alertId: string): Observable<boolean> {
    return this.http.delete<void>(`${this.apiUrl}/portfolios/${portfolioId}/alerts/${alertId}`).pipe(
      map(() => true),
      catchError(() => {
        this.toast.show('Could not delete alert.', 'error');
        return of(false);
      }),
    );
  }
}


