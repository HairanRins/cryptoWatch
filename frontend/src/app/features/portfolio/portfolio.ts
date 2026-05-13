import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PortfolioService } from '../../core/services/portfolio.service';

interface AssetHolding {
  name: string;
  symbol: string;
  icon: string;
  iconColor: string;
  iconBg: string;
  price: string;
  holdings: number;
  value: string;
  pnl: string;
  pnlPositive: boolean;
}

interface Transaction {
  date: string;
  type: 'BUY' | 'SELL';
  asset: string;
  amount: string;
  price: string;
  total: string;
  status: string;
}

@Component({
  selector: 'app-portfolio',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './portfolio.html',
  styleUrl: './portfolio.css',
})
export class PortfolioComponent implements OnInit {
  private readonly portfolioService = inject(PortfolioService);

  isLoading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;

    this.portfolioService.getPortfolios().subscribe({
      next: () => this.isLoading = false,
      error: () => {
        this.error = 'Failed to load portfolio data.';
        this.isLoading = false;
      },
    });
  }

  retry(): void {
    this.loadData();
  }

  navItems = [
    { label: 'Dashboard', active: false, route: '/dashboard' },
    { label: 'Markets', active: false, route: '/markets' },
    { label: 'Portfolio', active: true, route: '/portfolio' },
    { label: 'Alerts', active: false, route: '/alerts' },
  ];

  bottomNavItems = [
    { label: 'Support', active: false, route: '#' },
    { label: 'Logout', active: false, route: '#' },
  ];

  showModal = false;

  stats = [
    {
      label: 'TOTAL BALANCE',
      value: '$128,452.34',
      change: '+12.5% (24h)',
      positive: true,
      icon: 'account_balance_wallet',
      iconColor: 'text-primary',
    },
    {
      label: 'UNREALIZED P&L',
      value: '+$14,290.10',
      change: 'All-time high reached',
      positive: true,
      icon: 'payments',
      iconColor: 'text-secondary',
    },
    {
      label: 'TOTAL ASSETS',
      value: '18 Coins',
      change: 'Across 4 Networks',
      positive: true,
      icon: 'layers',
      iconColor: 'text-tertiary',
    },
    {
      label: 'STAKING YIELD',
      value: '5.2% APY',
      change: 'Estimated $680/mo',
      positive: true,
      icon: 'token',
      iconColor: 'text-primary-fixed-dim',
    },
  ];

  assets: AssetHolding[] = [
    { name: 'Bitcoin', symbol: 'BTC', icon: 'currency_bitcoin', iconColor: '#F7931A', iconBg: '#F7931A20', price: '$64,289.00', holdings: 1.24, value: '$79,718.36', pnl: '+24.8%', pnlPositive: true },
    { name: 'Ethereum', symbol: 'ETH', icon: 'eco', iconColor: '#627EEA', iconBg: '#627EEA20', price: '$3,452.12', holdings: 12.5, value: '$43,151.50', pnl: '+12.4%', pnlPositive: true },
    { name: 'Solana', symbol: 'SOL', icon: 'token', iconColor: '#14F195', iconBg: '#14F19520', price: '$142.10', holdings: 45.2, value: '$6,422.92', pnl: '-2.1%', pnlPositive: false },
  ];

  transactions: Transaction[] = [
    { date: 'Oct 12, 2023', type: 'BUY', asset: 'BTC', amount: '0.05', price: '$61,200.00', total: '$3,060.00', status: 'Completed' },
    { date: 'Oct 10, 2023', type: 'SELL', asset: 'ETH', amount: '2.50', price: '$3,840.12', total: '$9,600.30', status: 'Completed' },
    { date: 'Oct 08, 2023', type: 'BUY', asset: 'SOL', amount: '15.0', price: '$138.50', total: '$2,077.50', status: 'Completed' },
  ];

  filterType: 'all' | 'buy' | 'sell' = 'all';

  get filteredTransactions(): Transaction[] {
    if (this.filterType === 'all') return this.transactions;
    return this.transactions.filter(t => t.type === (this.filterType === 'buy' ? 'BUY' : 'SELL'));
  }

  setFilter(type: 'all' | 'buy' | 'sell'): void {
    this.filterType = type;
  }

  readonly donutCircumference = 2 * Math.PI * 16;

  allocationSegments = [
    { label: 'Bitcoin', percent: 62.1, color: 'stroke-primary', offset: 0, length: (62.1 / 100) * 100 },
    { label: 'Ethereum', percent: 33.6, color: 'stroke-secondary', offset: -62.1, length: (33.6 / 100) * 100 },
    { label: 'Others', percent: 4.3, color: 'stroke-tertiary', offset: -95.7, length: (4.3 / 100) * 100 },
  ];
}
