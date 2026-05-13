import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../shared/services/toast.service';

export interface Coin {
  id: string;
  symbol: string;
  name: string;
  image: string;
  currentPrice: number;
  priceChange24h: number;
  priceChangePercentage24h: number;
  marketCap: number;
  totalVolume: number;
  high24h: number;
  low24h: number;
  circulatingSupply: number;
}

export interface CoinDetail {
  id: string;
  symbol: string;
  name: string;
  image: string;
  description: string;
  currentPrice: number;
  priceChangePercentage24h: number;
  marketCap: number;
  totalVolume: number;
  high24h: number;
  low24h: number;
  circulatingSupply: number;
  ath: number;
  athDate: string;
}

export interface PriceHistory {
  prices: [number, number][];
}

@Injectable({ providedIn: 'root' })
export class CoinService {
  private readonly http = inject(HttpClient);
  private readonly toast = inject(ToastService);
  private readonly apiUrl = `${environment.apiUrl}/coins`;

  getCoins(): Observable<Coin[]> {
    return this.http.get<Coin[]>(this.apiUrl).pipe(
      catchError(() => {
        this.toast.show('Could not load market data. Using cached data.', 'warning');
        return of([]);
      }),
    );
  }

  getCoinDetail(coinId: string): Observable<CoinDetail | null> {
    return this.http.get<CoinDetail>(`${this.apiUrl}/${coinId}`).pipe(
      catchError(() => {
        this.toast.show(`Could not load details for ${coinId}.`, 'warning');
        return of(null);
      }),
    );
  }

  getPriceHistory(coinId: string, period = '7d'): Observable<PriceHistory | null> {
    const params = new HttpParams().set('period', period);
    return this.http.get<PriceHistory>(`${this.apiUrl}/${coinId}/history`, { params }).pipe(
      catchError(() => {
        this.toast.show('Could not load price history.', 'warning');
        return of(null);
      }),
    );
  }
}
