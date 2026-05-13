import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../shared/services/toast.service';

export interface Portfolio {
  id: string;
  name: string;
  description: string;
  totalValue: number;
  totalPnl: number;
  totalPnlPercentage: number;
}

export interface Asset {
  id: string;
  coinId: string;
  symbol: string;
  name: string;
  quantity: number;
  purchasePrice: number;
  currentPrice: number;
  value: number;
  pnl: number;
  pnlPercentage: number;
}

export interface Transaction {
  id: string;
  portfolioId: string;
  coinId: string;
  symbol: string;
  type: 'BUY' | 'SELL';
  quantity: number;
  price: number;
  total: number;
  date: string;
  status: string;
}

export interface CreateTransactionRequest {
  coinId: string;
  type: 'BUY' | 'SELL';
  quantity: number;
  price: number;
}

@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private readonly http = inject(HttpClient);
  private readonly toast = inject(ToastService);
  private readonly apiUrl = `${environment.apiUrl}/portfolios`;

  getPortfolios(): Observable<Portfolio[]> {
    return this.http.get<Portfolio[]>(this.apiUrl).pipe(
      catchError(() => {
        this.toast.show('Could not load portfolios.', 'warning');
        return of([]);
      }),
    );
  }

  getPortfolio(id: string): Observable<Portfolio | null> {
    return this.http.get<Portfolio>(`${this.apiUrl}/${id}`).pipe(
      catchError(() => {
        this.toast.show('Could not load portfolio details.', 'warning');
        return of(null);
      }),
    );
  }

  createPortfolio(name: string, description: string): Observable<Portfolio | null> {
    return this.http.post<Portfolio>(this.apiUrl, { name, description }).pipe(
      catchError(() => {
        this.toast.show('Could not create portfolio.', 'error');
        return of(null);
      }),
    );
  }

  deletePortfolio(id: string): Observable<boolean> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      map(() => true),
      catchError(() => {
        this.toast.show('Could not delete portfolio.', 'error');
        return of(false);
      }),
    );
  }

  getAssets(portfolioId: string): Observable<Asset[]> {
    return this.http.get<Asset[]>(`${this.apiUrl}/${portfolioId}/assets`).pipe(
      catchError(() => {
        this.toast.show('Could not load assets.', 'warning');
        return of([]);
      }),
    );
  }

  getTransactions(portfolioId: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/${portfolioId}/transactions`).pipe(
      catchError(() => {
        this.toast.show('Could not load transactions.', 'warning');
        return of([]);
      }),
    );
  }

  addTransaction(portfolioId: string, request: CreateTransactionRequest): Observable<Transaction | null> {
    return this.http.post<Transaction>(`${this.apiUrl}/${portfolioId}/transactions`, request).pipe(
      catchError(() => {
        this.toast.show('Could not add transaction.', 'error');
        return of(null);
      }),
    );
  }

  deleteTransaction(transactionId: string): Observable<boolean> {
    return this.http.delete<void>(`${this.apiUrl}/transactions/${transactionId}`).pipe(
      map(() => true),
      catchError(() => {
        this.toast.show('Could not delete transaction.', 'error');
        return of(false);
      }),
    );
  }
}


