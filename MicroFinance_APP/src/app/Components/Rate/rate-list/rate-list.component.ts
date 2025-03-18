import { Component, OnInit } from '@angular/core';
import { RateService } from '../../../service/rate.service';
import { Router } from '@angular/router';
import { Rate } from '../../../model/Rate';

@Component({
  selector: 'app-list-rate',
  templateUrl: './rate-list.component.html',
  styleUrls: ['./rate-list.component.css'],
  standalone: false
})
export class RateListComponent implements OnInit {
  rates: Rate[] = [];

  constructor(private rateService: RateService, private router: Router) { }

  ngOnInit(): void {
    this.loadRates();
  }

  loadRates(): void {
    this.rateService.getAllRates().subscribe({
      next: (response: any) => {
        this.rates = response.map((rate: any) => ({
          id: rate.id || rate.rateId,
          rateType: rate.rateType,
          value: rate.value,
          status: rate.status
        }));
        console.log('Loaded rates:', this.rates);
      },
      error: (error) => {
        console.error('Error retrieving rates', error);
        alert('Error retrieving rates');  
          
      }
    });
  }

  editRate(rate: Rate): void {
    if (!rate.id) {
      console.error('Rate ID is missing:', rate);
      alert('Cannot edit rate: ID is missing');
      return;
    }
    // Navigate with rate data
    this.router.navigate(['/dashboard/update-rate', rate.id], {
      state: { rateData: rate }
    });
  }
}
