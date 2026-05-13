import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CoinService } from '../../core/services/coin.service';

interface MarketPair {
  pair: string;
  name: string;
  icon: string;
  price: string;
  change24h: number;
  volume24h: string;
  marketCap: string;
  high24h: string;
  low24h: string;
  supply: string;
}

interface Gainer {
  name: string;
  symbol: string;
  price: string;
  change: number;
  icon: string;
}

@Component({
  selector: 'app-markets',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './markets.html',
  styleUrl: './markets.css',
})
export class MarketsComponent implements OnInit {
  private readonly coinService = inject(CoinService);

  isLoading = false;
  error: string | null = null;
  searchQuery = '';

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.error = null;

    this.coinService.getCoins().subscribe({
      next: () => this.isLoading = false,
      error: () => {
        this.error = 'Failed to load market data.';
        this.isLoading = false;
      },
    });
  }

  retry(): void {
    this.loadData();
  }

  navItems = [
    { label: 'Dashboard', active: false, route: '/' },
    { label: 'Markets', active: true, route: '/markets' },
    { label: 'Portfolio', active: false, route: '/portfolio' },
    { label: 'Alerts', active: false, route: '/alerts' },
  ];

  bottomNavItems = [
    { label: 'Support', active: false, route: '#' },
    { label: 'Logout', active: false, route: '#' },
  ];

  marketStats = [
    { label: 'MARKET CAP', value: '$2.48T', change: '+1.2%', positive: true },
    { label: '24H VOLUME', value: '$186.4B', change: '+8.7%', positive: true },
    { label: 'BTC DOMINANCE', value: '54.2%', change: '-0.8%', positive: false },
    { label: 'ACTIVE PAIRS', value: '845', change: '+12', positive: true },
  ];

  gainers: Gainer[] = [
    { name: 'Solana', symbol: 'SOL', price: '$178.42', change: 21.8, icon: 'S' },
    { name: 'Render', symbol: 'RNDR', price: '$8.94', change: 15.3, icon: 'R' },
    { name: 'Celestia', symbol: 'TIA', price: '$12.67', change: 12.1, icon: 'C' },
    { name: 'Injective', symbol: 'INJ', price: '$34.21', change: 9.7, icon: 'I' },
    { name: 'Sei', symbol: 'SEI', price: '$0.82', change: 8.4, icon: 'S' },
  ];

  losers: Gainer[] = [
    { name: 'Avalanche', symbol: 'AVAX', price: '$35.12', change: -4.2, icon: 'A' },
    { name: 'Chainlink', symbol: 'LINK', price: '$14.67', change: -3.1, icon: 'L' },
    { name: 'Arbitrum', symbol: 'ARB', price: '$1.12', change: -2.8, icon: 'A' },
    { name: 'Aptos', symbol: 'APT', price: '$9.34', change: -2.1, icon: 'A' },
    { name: 'Polkadot', symbol: 'DOT', price: '$7.45', change: -1.5, icon: 'D' },
  ];

  pairs: MarketPair[] = [
    { pair: 'BTC/USDT', name: 'Bitcoin', icon: 'B', price: '$67,432.10', change24h: 2.4, volume24h: '28.4B', marketCap: '1.32T', high24h: '$68,120', low24h: '$65,890', supply: '19.7M' },
    { pair: 'ETH/USDT', name: 'Ethereum', icon: 'E', price: '$3,456.80', change24h: 1.8, volume24h: '15.2B', marketCap: '415.8B', high24h: '$3,520', low24h: '$3,380', supply: '120.2M' },
    { pair: 'SOL/USDT', name: 'Solana', icon: 'S', price: '$178.42', change24h: 21.8, volume24h: '8.7B', marketCap: '78.2B', high24h: '$182', low24h: '$155', supply: '438.5M' },
    { pair: 'BNB/USDT', name: 'BNB', icon: 'B', price: '$578.90', change24h: 0.5, volume24h: '3.1B', marketCap: '89.1B', high24h: '$582', low24h: '$571', supply: '153.9M' },
    { pair: 'XRP/USDT', name: 'XRP', icon: 'X', price: '$0.62', change24h: -1.2, volume24h: '2.8B', marketCap: '33.7B', high24h: '$0.64', low24h: '$0.61', supply: '54.3B' },
    { pair: 'ADA/USDT', name: 'Cardano', icon: 'A', price: '$0.48', change24h: 3.2, volume24h: '1.9B', marketCap: '16.8B', high24h: '$0.49', low24h: '$0.46', supply: '35.0B' },
    { pair: 'DOGE/USDT', name: 'Dogecoin', icon: 'D', price: '$0.15', change24h: 5.6, volume24h: '2.1B', marketCap: '21.4B', high24h: '$0.16', low24h: '$0.14', supply: '142.5B' },
    { pair: 'DOT/USDT', name: 'Polkadot', icon: 'D', price: '$7.45', change24h: -1.5, volume24h: '1.2B', marketCap: '10.2B', high24h: '$7.68', low24h: '$7.32', supply: '1.37B' },
    { pair: 'MATIC/USDT', name: 'Polygon', icon: 'M', price: '$0.72', change24h: 4.1, volume24h: '0.9B', marketCap: '6.7B', high24h: '$0.74', low24h: '$0.69', supply: '9.3B' },
    { pair: 'AVAX/USDT', name: 'Avalanche', icon: 'A', price: '$35.12', change24h: -4.2, volume24h: '1.5B', marketCap: '13.4B', high24h: '$36.80', low24h: '$34.50', supply: '381.2M' },
    { pair: 'RNDR/USDT', name: 'Render', icon: 'R', price: '$8.94', change24h: 15.3, volume24h: '0.7B', marketCap: '3.4B', high24h: '$9.12', low24h: '$7.88', supply: '376.5M' },
    { pair: 'INJ/USDT', name: 'Injective', icon: 'I', price: '$34.21', change24h: 9.7, volume24h: '0.5B', marketCap: '3.1B', high24h: '$34.80', low24h: '$31.20', supply: '90.5M' },
  ];

  sortColumn = 'volume24h';
  sortDirection: 'asc' | 'desc' = 'desc';

  get filteredPairs(): MarketPair[] {
    return this.pairs.filter(p =>
      p.pair.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      p.name.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  get sortedPairs(): MarketPair[] {
    const sorted = [...this.filteredPairs];
    sorted.sort((a, b) => {
      let cmp = 0;
      switch (this.sortColumn) {
        case 'pair': cmp = a.pair.localeCompare(b.pair); break;
        case 'price': cmp = parseFloat(a.price.replace(/[$,]/g, '')) - parseFloat(b.price.replace(/[$,]/g, '')); break;
        case 'change24h': cmp = a.change24h - b.change24h; break;
        case 'volume24h': cmp = parseFloat(a.volume24h.replace(/[B]/g, '')) - parseFloat(b.volume24h.replace(/[B]/g, '')); break;
        case 'marketCap': cmp = parseFloat(a.marketCap.replace(/[BT]/g, '')) - parseFloat(b.marketCap.replace(/[BT]/g, '')); break;
        default: cmp = 0;
      }
      return this.sortDirection === 'asc' ? cmp : -cmp;
    });
    return sorted;
  }

  sortBy(column: string): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'desc';
    }
  }

  getSortIcon(column: string): string {
    if (this.sortColumn !== column) return 'unfold_more';
    return this.sortDirection === 'asc' ? 'arrow_upward' : 'arrow_downward';
  }

  readonly sparklinePath = 'M0,30 Q20,28 40,32 T60,25 T80,27 T100,20 T120,22 T140,15';

  readonly chartPoints = [
    { x: 0, y: 120 },
    { x: 117, y: 140 },
    { x: 233, y: 95 },
    { x: 350, y: 155 },
    { x: 466, y: 35 },
    { x: 583, y: 85 },
    { x: 700, y: 60 },
  ];

  get chartPath(): string {
    return this.catmullRomToBezier(this.chartPoints);
  }

  get chartAreaPath(): string {
    return `${this.chartPath} L 700,180 L 0,180 Z`;
  }

  private catmullRomToBezier(points: { x: number; y: number }[]): string {
    if (points.length < 2) return '';
    const p = [points[0], ...points, points[points.length - 1]];
    const cmds: string[] = [`M ${points[0].x},${points[0].y}`];
    for (let i = 1; i < p.length - 2; i++) {
      const p0 = p[i - 1], p1 = p[i], p2 = p[i + 1], p3 = p[i + 2];
      cmds.push(`C ${p1.x + (p2.x - p0.x) / 6},${p1.y + (p2.y - p0.y) / 6} ${p2.x - (p3.x - p1.x) / 6},${p2.y - (p3.y - p1.y) / 6} ${p2.x},${p2.y}`);
    }
    return cmds.join(' ');
  }

  readonly donutCircumference = 2 * Math.PI * 52;

  get donutSegments() {
    const segments: { offset: number; length: number; color: string; label: string; percent: number }[] = [];
    const data = [
      { label: 'BTC', percent: 52, color: '#4d8eff' },
      { label: 'ETH', percent: 28, color: '#4edea3' },
      { label: 'Others', percent: 20, color: '#ffb3ad' },
    ];
    let cumulative = 0;
    for (const item of data) {
      const length = (item.percent / 100) * this.donutCircumference;
      segments.push({ offset: -cumulative, length, color: item.color, label: item.label, percent: item.percent });
      cumulative += length;
    }
    return segments;
  }
}
