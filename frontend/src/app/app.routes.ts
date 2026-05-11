import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard';
import { MarketsComponent } from './features/markets/markets';
import { PortfolioComponent } from './features/portfolio/portfolio';
import { AlertsComponent } from './features/alerts/alerts';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'markets', component: MarketsComponent },
  { path: 'portfolio', component: PortfolioComponent },
  { path: 'alerts', component: AlertsComponent },
];
