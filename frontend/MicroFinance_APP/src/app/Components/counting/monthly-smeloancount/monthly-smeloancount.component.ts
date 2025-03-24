// src/app/monthly-smeloancount/monthly-smeloancount.component.ts
import { Component, OnInit } from '@angular/core';
import { Chart, LinearScale, BarController, BarElement, Title, Tooltip, Legend, CategoryScale } from 'chart.js';
import { SmeLoanService } from '../../../service/sme-loan.service';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';
import { MonthlySMELoanCount } from '../../../model/MonthlySMELoanCount';

// Register all required Chart.js components
Chart.register(LinearScale, BarController, BarElement, Title, Tooltip, Legend, CategoryScale);

@Component({
  selector: 'app-monthly-smeloancount',
  standalone: false,
  templateUrl: './monthly-smeloancount.component.html',
  styleUrl: './monthly-smeloancount.component.css'
})
export class MonthlySMELoancountComponent implements OnInit {
  isLoggedIn: boolean = false;
  chart: Chart | undefined;
  allData: MonthlySMELoanCount[] = [];
  filteredData: MonthlySMELoanCount[] = [];
  years: number[] = [];
  selectedYear: number = new Date().getFullYear(); // Default to current year
  pageSize = 6; // 6 combined periods (12 months)
  currentPage = 1;
  totalPages = 1;

  constructor(
    private smeloanservice: SmeLoanService,
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
    this.smeloanservice.getMonthlyApprovedLoans().subscribe({
      next: (data: MonthlySMELoanCount[]) => {
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
    // Filter by selected year
    this.filteredData = this.allData.filter(item => item.month.startsWith(this.selectedYear.toString()));

    // Group every two months
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
    if (this.chart) {
        this.chart.destroy();
    }

    this.chart = new Chart('loanChart', {
        type: 'bar',
        data: {
            labels: paginatedData.map(item => item.period),
            datasets: [{
                label: '', // No label here since we will draw it below
                data: paginatedData.map(item => item.count),
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
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
                    text: `Monthly Approved SME Loans (${this.selectedYear})`,
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
    this.currentPage = 1; // Reset to first page
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