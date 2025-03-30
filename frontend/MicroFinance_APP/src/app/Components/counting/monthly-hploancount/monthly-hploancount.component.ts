import { Component, OnDestroy, OnInit, ElementRef, ViewChild, Input, SimpleChanges, OnChanges, ChangeDetectorRef } from '@angular/core';
import { MonthlyHpLoanCount } from '../../../model/MonthlyHpLoanCount';
import { Chart } from 'chart.js';
import { HpLoanService } from '../../../service/hp-loan.service';
import { AuthService } from '../../../service/auth.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-monthly-hploancount',
  standalone: false,
  templateUrl: './monthly-hploancount.component.html',
  styleUrls: ['./monthly-hploancount.component.css']
})
export class MonthlyHploancountComponent implements OnInit, OnChanges {
  @ViewChild('loanChart', { static: false }) loanChartCanvas!: ElementRef<HTMLCanvasElement>;
  @Input() branchId: number = 0;
  
  isLoggedIn: boolean = false;
  chart: Chart | undefined;
  allData: MonthlyHpLoanCount[] = [];
  filteredData: MonthlyHpLoanCount[] = [];
  years: number[] = [];
  selectedYear: number = new Date().getFullYear();
  pageSize = 6;
  currentPage = 1;
  totalPages = 1;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private hpLoanService: HpLoanService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,

  ) {}

  ngOnInit(): void {
    this.isLoggedIn = !!this.authService.getAccessToken();
    if (this.isLoggedIn) {
      if (this.branchId) {
        this.loadData();
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['branchId'] && !changes['branchId'].firstChange && this.branchId) {
      this.resetComponent();
      this.loadData();
    }
  }

  resetComponent(): void {
    this.allData = [];
    this.filteredData = [];
    this.years = [];
    this.currentPage = 1;
    this.totalPages = 1;
    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }
  }

  loadData(): void {
    this.hpLoanService.getMonthlyApprovedLoansByBranch(this.branchId).subscribe({
      next: (data: MonthlyHpLoanCount[]) => {
        this.allData = data;
        this.years = [...new Set(data.map(item => parseInt(item.month.split('-')[0])))].sort();
        this.cdr.detectChanges(); // Ensure view is updated
        this.filterAndGroupData();
      },
      error: (err) => {
        console.error('Error loading chart data:', err);
        this.errorMessage = 'Failed to load HP loan data';
        this.loading = false;
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

    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }

    this.chart = new Chart(this.loanChartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: paginatedData.map(item => item.period),
        datasets: [{
          label: '',
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
          y: { beginAtZero: true },
          x: {}
        },
        plugins: {
          legend: { display: false },
          title: {
            display: true,
            text: `Monthly Approved HP Loans - Branch ${this.branchId} (${this.selectedYear})`,
            font: { size: 16 }
          }
        }
      }
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