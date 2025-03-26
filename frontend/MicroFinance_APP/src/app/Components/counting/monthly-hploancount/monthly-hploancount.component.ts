import { Component, OnDestroy, OnInit, ElementRef, ViewChild } from '@angular/core';
import { MonthlyHpLoanCount } from '../../../model/MonthlyHpLoanCount';
import { Chart } from 'chart.js';
import { HpLoanService } from '../../../service/hp-loan.service';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-monthly-hploancount',
  standalone: false,
  templateUrl: './monthly-hploancount.component.html',
  styleUrl: './monthly-hploancount.component.css'
})
export class MonthlyHploancountComponent implements OnInit {
  @ViewChild('loanChart', { static: false }) loanChartCanvas!: ElementRef<HTMLCanvasElement>;
  isLoggedIn: boolean = false;
  chart: Chart | undefined;
  allData: MonthlyHpLoanCount[] = [];
  filteredData: MonthlyHpLoanCount[] = [];
  years: number[] = [];
  selectedYear: number = new Date().getFullYear();
  pageSize = 6; // 6 combined periods (12 months)
  currentPage = 1;
  totalPages = 1;

  constructor(
    private hpLoanService: HpLoanService, // Changed to HpLoanService
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = !!this.authService.getAccessToken();
    if (this.isLoggedIn) {
      this.loadData();
    } else {
      this.router.navigate(['/login']);
    }
  }

  loadData(): void {
    this.hpLoanService.getMonthlyApprovedLoans().subscribe({
      next: (data: MonthlyHpLoanCount[]) => {
        this.allData = data;
        this.years = [...new Set(data.map(item => parseInt(item.month.split('-')[0])))].sort();
        this.filterAndGroupData();
      },
      error: (err) => {
        console.error('Error loading chart data:', err);
        if (err.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }
      }
    });
  }

  filterAndGroupData(): void {
    this.filteredData = this.allData.filter(item => item.month.startsWith(this.selectedYear.toString()));

    const groupedData: { period: string; count: number }[] = [];
    for (let i = 0; i < 12; i += 2) {
      const month1 = `${this.selectedYear}-${String(i + 1).padStart(2, '0')}`;
      const month2 = `${this.selectedYear}-${String(i + 2).padStart(2, '0')}`;
      const month1Data = this.filteredData.find(d => d.month === month1) || { month: month1, approvedLoanCount: 0 };
      const month2Data = this.filteredData.find(d => d.month === month2) || { month: month2, approvedLoanCount: 0 };
      groupedData.push({
        period: `${this.getMonthName(i + 1)}-${this.getMonthName(i + 2)}`,
        count: month1Data.approvedLoanCount + month2Data.approvedLoanCount
      });
    }

    this.totalPages = Math.ceil(groupedData.length / this.pageSize);
    this.renderChart(groupedData);
  }

  renderChart(groupedData: { period: string; count: number }[]): void {
    const startIdx = (this.currentPage - 1) * this.pageSize;
    const endIdx = startIdx + this.pageSize;
    const paginatedData = groupedData.slice(startIdx, endIdx);

    const branchId = this.authService.getCurrentUserBranchId();

    // Destroy existing chart if it exists
    if (this.chart) {
        this.chart.destroy();
        this.chart = undefined; // Ensure reference is cleared
    }

    // Create new chart
    this.chart = new Chart(this.loanChartCanvas.nativeElement, {
        type: 'bar',
        data: {
            labels: paginatedData.map(item => item.period),
            datasets: [{
                label: '', // No label here since we will draw it below
                data: paginatedData.map(item => item.count),
                backgroundColor: 'rgb(20, 122, 231)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                },
                x: {}
            },
            plugins: {
                legend: {
                    display: false // Hide the legend
                },
                title: {
                    display: true,
                    text: `Monthly Approved HP Loans (${this.selectedYear})`,
                    font: { size: 16 }
                }
            }
        },
        plugins: [{
            id: 'customPlugin',
            afterDraw: (chart) => {
                const ctx = chart.ctx;
                const fontSize = 14;
                ctx.save();
                ctx.font = `${fontSize}px Arial`;
                ctx.fillStyle = 'rgba(0, 0, 0, 0.87)'; // Text color
                ctx.textAlign = 'center';
                ctx.textBaseline = 'bottom';
                const x = chart.chartArea.left + (chart.chartArea.right - chart.chartArea.left) / 2;
                const y = chart.chartArea.bottom + fontSize + 10; // Position below the chart
                ctx.restore();
            }
        }]
    });
}

  onYearChange(event: Event): void {
    this.selectedYear = parseInt((event.target as HTMLSelectElement).value);
    this.currentPage = 1;
    this.filterAndGroupData();
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.filterAndGroupData();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.filterAndGroupData();
    }
  }

  getMonthName(monthNum: number): string {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return months[monthNum - 1];
  }
}