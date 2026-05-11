import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface NavItem {
  label: string;
  active: boolean;
}

interface StatCard {
  label: string;
  value: string;
  change?: string;
  changeLabel?: string;
  positive: boolean;
  isGainer?: boolean;
  gainerSymbol?: string;
  isAlert?: boolean;
  sublabel?: string;
}

interface Activity {
  asset: string;
  type: 'BUY' | 'SELL';
  amount: string;
  status: 'COMPLETED' | 'PROCESSING';
  date: string;
}

interface AllocationItem {
  label: string;
  percent: number;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent {
  activePeriod = '1W';
  periods = ['1D', '1W', '1M', 'ALL'];

  navItems: NavItem[] = [
    { label: 'Dashboard', active: true },
    { label: 'Markets', active: false },
    { label: 'Portfolio', active: false },
    { label: 'Alerts', active: false },
  ];

  bottomNavItems: NavItem[] = [
    { label: 'Support', active: false },
    { label: 'Logout', active: false },
  ];

  stats: StatCard[] = [
    {
      label: 'TOTAL BALANCE',
      value: '$124,592.42',
      change: '+12.4%',
      changeLabel: '(30d)',
      positive: true,
    },
    {
      label: '24H PROFIT/LOSS',
      value: '+$4,210.15',
      change: '+3.5%',
      positive: true,
    },
    {
      label: 'TOP GAINER (24H)',
      value: 'Solana',
      change: '+21.8%',
      positive: true,
      isGainer: true,
      gainerSymbol: 'SOL',
    },
    {
      label: 'ACTIVE ALERTS',
      value: '08',
      positive: true,
      isAlert: true,
      sublabel: '2 pending actions',
    },
  ];

  activities: Activity[] = [
    { asset: 'BTC', type: 'BUY', amount: '0.42 BTC', status: 'COMPLETED', date: 'Nov 24, 14:20' },
    { asset: 'ETH', type: 'SELL', amount: '1.50 ETH', status: 'COMPLETED', date: 'Nov 23, 09:15' },
    { asset: 'SOL', type: 'BUY', amount: '25.00 SOL', status: 'PROCESSING', date: 'Nov 23, 08:42' },
  ];

  allocation: AllocationItem[] = [
    { label: 'Bitcoin (BTC)', percent: 62, color: '#4d8eff' },
    { label: 'Ethereum (ETH)', percent: 28, color: '#4edea3' },
    { label: 'Others', percent: 10, color: '#ffb3ad' },
  ];

  // Portfolio growth chart points
  private readonly chartPoints = [
    { x: 0, y: 120 },
    { x: 117, y: 140 },
    { x: 233, y: 95 },
    { x: 350, y: 155 },
    { x: 466, y: 35 },
    { x: 583, y: 85 },
    { x: 700, y: 60 },
  ];

  get portfolioPath(): string {
    return this.catmullRomToBezier(this.chartPoints);
  }

  get portfolioAreaPath(): string {
    const path = this.portfolioPath;
    return `${path} L 700,180 L 0,180 Z`;
  }

  // Donut chart
  readonly donutRadius = 52;
  readonly donutCircumference = 2 * Math.PI * 52;

  get donutSegments(): { offset: number; length: number; color: string }[] {
    const segments: { offset: number; length: number; color: string }[] = [];
    let cumulative = 0;
    for (const item of this.allocation) {
      const length = (item.percent / 100) * this.donutCircumference;
      segments.push({
        offset: -cumulative,
        length,
        color: item.color,
      });
      cumulative += length;
    }
    return segments;
  }

  // Global cap sparkline
  readonly sparklinePoints = [
    { x: 0, y: 30 },
    { x: 20, y: 28 },
    { x: 40, y: 32 },
    { x: 60, y: 25 },
    { x: 80, y: 27 },
    { x: 100, y: 20 },
    { x: 120, y: 22 },
    { x: 140, y: 15 },
  ];

  get sparklinePath(): string {
    return this.catmullRomToBezier(this.sparklinePoints);
  }

  setPeriod(period: string): void {
    this.activePeriod = period;
  }

  private catmullRomToBezier(
    points: { x: number; y: number }[],
  ): string {
    if (points.length < 2) return '';
    const p = [points[0], ...points, points[points.length - 1]];
    const cmds: string[] = [`M ${points[0].x},${points[0].y}`];

    for (let i = 1; i < p.length - 2; i++) {
      const p0 = p[i - 1];
      const p1 = p[i];
      const p2 = p[i + 1];
      const p3 = p[i + 2];
      const cp1x = p1.x + (p2.x - p0.x) / 6;
      const cp1y = p1.y + (p2.y - p0.y) / 6;
      const cp2x = p2.x - (p3.x - p1.x) / 6;
      const cp2y = p2.y - (p3.y - p1.y) / 6;
      cmds.push(`C ${cp1x},${cp1y} ${cp2x},${cp2y} ${p2.x},${p2.y}`);
    }
    return cmds.join(' ');
  }
}
