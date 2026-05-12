import { Routes } from '@angular/router';
import { LandingComponent } from './features/landing/landing';
import { DashboardComponent } from './features/dashboard/dashboard';
import { MarketsComponent } from './features/markets/markets';
import { PortfolioComponent } from './features/portfolio/portfolio';
import { AlertsComponent } from './features/alerts/alerts';
import { SignupComponent } from './features/auth/signup/signup';
import { LoginComponent } from './features/auth/login/login';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'markets', component: MarketsComponent },
  { path: 'portfolio', component: PortfolioComponent },
  { path: 'alerts', component: AlertsComponent },
];
