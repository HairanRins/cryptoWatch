import { Component } from '@angular/core';
import { DashboardComponent } from './features/dashboard/dashboard';

@Component({
  selector: 'app-root',
  imports: [DashboardComponent],
  template: '<app-dashboard />',
})
export class App {}
