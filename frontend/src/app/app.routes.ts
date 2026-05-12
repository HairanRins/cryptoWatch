import { Routes } from '@angular/router';
import { LandingComponent } from './features/landing/landing';
import { DashboardComponent } from './features/dashboard/dashboard';
import { MarketsComponent } from './features/markets/markets';
import { PortfolioComponent } from './features/portfolio/portfolio';
import { AlertsComponent } from './features/alerts/alerts';
import { SignupComponent } from './features/auth/signup/signup';
import { LoginComponent } from './features/auth/login/login';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'signup', component: SignupComponent, canActivate: [guestGuard] },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'markets', component: MarketsComponent, canActivate: [authGuard] },
  { path: 'portfolio', component: PortfolioComponent, canActivate: [authGuard] },
  { path: 'alerts', component: AlertsComponent, canActivate: [authGuard] },
];
