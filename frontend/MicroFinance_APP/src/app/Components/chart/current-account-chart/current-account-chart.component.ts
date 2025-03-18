import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);


@Component({
  selector: 'app-current-account-chart',
  standalone: false,
  templateUrl: './current-account-chart.component.html',
  styleUrl: './current-account-chart.component.css'
})
export class CurrentAccountChartComponent implements OnInit {

  chart: any;
  apiUrl = 'http://localhost:8081/accounts/list';
  
    constructor(private http:HttpClient) { }

  ngOnInit() {
    this.fetchCurrentAccounts();
  }

  fetchCurrentAccounts() {
    this.http.get<any>(this.apiUrl).subscribe(response => {
      console.log('API Response:', response); // Debugging line
  
      if (!response || !response.data || !Array.isArray(response.data)) {
        console.error('Invalid API response format:', response);
        return;
      }
  
      const accountIds = response.data.map((acc: { accountId: string }) => acc.accountId);
      const totalBalances = response.data.map((acc: { totalBalance: number }) => acc.totalBalance);
  
      this.createChart(accountIds, totalBalances);
    }, error => {
      console.error('Error fetching current accounts:', error);
    });
  }
  
  createChart(accountIds: string[], balances: number[]) {
    this.chart = new Chart('currentAccountChart', {
      type: 'bar',
      data: {
        labels: accountIds,
        datasets: [{
          label: 'Total Balance',
          data: balances,
          backgroundColor: 'rgba(54, 162, 235, 0.5)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }

}
