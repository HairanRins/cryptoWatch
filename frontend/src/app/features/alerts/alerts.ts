import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../core/services/alert.service';

interface ActiveAlert {
  asset: string;
  assetName: string;
  assetColor: string;
  condition: string;
  price: string;
  channels: string[];
  created: string;
  recurring: boolean;
  lastChecked: string;
  enabled: boolean;
  paused?: boolean;
  pausedDate?: string;
}

interface TriggeredEvent {
  event: string;
  value: string;
  channel?: string;
  channels?: string[];
  timestamp: string;
  type: 'success' | 'warning';
}

@Component({
  selector: 'app-alerts',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './alerts.html',
  styleUrl: './alerts.css',
})
export class AlertsComponent implements OnInit {
  private readonly alertService = inject(AlertService);

  isLoading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;
  }

  retry(): void {
    this.loadData();
  }

  navItems = [
    { label: 'Dashboard', active: false, route: '/dashboard' },
    { label: 'Markets', active: false, route: '/markets' },
    { label: 'Portfolio', active: false, route: '/portfolio' },
    { label: 'Alerts', active: true, route: '/alerts' },
  ];

  bottomNavItems = [
    { label: 'Support', active: false, route: '#' },
    { label: 'Logout', active: false, route: '#' },
  ];

  activeAlerts: ActiveAlert[] = [
    { asset: 'BTC', assetName: 'Bitcoin', assetColor: '#F7931A', condition: 'Above', price: '70,000.00', channels: ['Push'], created: 'Jan 12, 2024', recurring: true, lastChecked: '2 mins ago', enabled: true },
    { asset: 'ETH', assetName: 'Ethereum', assetColor: '#627EEA', condition: 'Below', price: '3,200.00', channels: ['Push', 'Email'], created: 'Feb 02, 2024', recurring: false, lastChecked: 'Just now', enabled: true },
    { asset: 'SOL', assetName: 'Solana', assetColor: '#14F195', condition: 'Above', price: '120.00', channels: ['SMS'], created: 'Feb 15, 2024', recurring: true, lastChecked: 'Feb 20', enabled: false, paused: true, pausedDate: 'Feb 20' },
  ];

  triggeredLogs: TriggeredEvent[] = [
    { event: 'BTC Price Above $62,000', value: '$62,105.42', channel: 'smartphone', timestamp: '12:04:12 UTC', type: 'success' },
    { event: 'ETH Price Below $2,800', value: '$2,794.10', channels: ['smartphone', 'mail'], timestamp: 'Yesterday', type: 'success' },
    { event: 'API Connection Timeout', value: 'ERR_TIMEOUT', channel: 'terminal', timestamp: 'Feb 14', type: 'warning' },
  ];

  globalSettings = {
    systemHealth: true,
    weeklySummary: false,
  };

  createAlert = {
    asset: 'Bitcoin (BTC)',
    condition: 'above',
    targetPrice: '70,000.00',
    push: true,
    email: true,
    sms: false,
  };

  searchQuery = '';
}
