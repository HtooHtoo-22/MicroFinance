import { Component, OnInit } from '@angular/core';
import { Rate } from '../../../model/Rate';
import { ActivatedRoute, Router } from '@angular/router';
import { RateService } from '../../../service/rate.service';

@Component({
  selector: 'app-update-rate',
  standalone: false,
  templateUrl: './update-rate.component.html',
  styleUrl: './update-rate.component.css'
})
export class UpdateRateComponent  implements OnInit {
  rateId!: number;
  rate: Rate = {
    rateType: '',
    value: 0,
    status: false
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private rateService: RateService
  ) {
    // Get rate data from router state if available
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { rateData: Rate };
    
    if (state && state.rateData) {
      console.log('Received rate data:', state.rateData);
      this.rate = { ...state.rateData };
      this.rateId = state.rateData.id!;
    }
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      console.log('Rate ID from route:', idParam);
      
      if (!idParam) {
        console.error('No ID parameter provided');
        alert('No rate ID provided');
        this.router.navigate(['/admin-dashboard/list-rate']);
        return;
      }

      const parsedId = parseInt(idParam, 10);
      if (isNaN(parsedId) || parsedId <= 0) {
        console.error('Invalid ID parameter:', idParam);
        alert('Invalid rate ID format');
        this.router.navigate(['/admin-dashboard/list-rate']);
        return;
      }

      this.rateId = parsedId;
      
      // Only load rate if we didn't get it from state
      if (!this.rate.rateType) {
        this.loadRate(this.rateId);
      }
    });
  }

  loadRate(id: number): void {
    console.log('Loading rate with ID:', id);
    this.rateService.getRateById(id).subscribe({
      next: (data: Rate) => {
        console.log('Received rate data:', data);
        if (data) {
          this.rate = data;
        } else {
          console.error('No rate data received for ID:', id);
          alert('Rate not found');
          this.router.navigate(['/admin-dashboard/list-rate']);
        }
      },
      error: (error) => {
        console.error('Error retrieving rate:', error);
        alert('Error retrieving rate: ' + (error.message || 'Unknown error'));
        this.router.navigate(['/admin-dashboard/list-rate']);
      }
    });
  }

  updateRate(): void {
    if (!this.rateId) {
      alert('No rate ID found');
      return;
    }

    const rateToUpdate: Rate = {
      rateType: this.rate.rateType,
      value: this.rate.value,
      status: this.rate.status
    };

    this.rateService.updateRate(this.rateId, rateToUpdate).subscribe({
      next: (updatedRate: Rate) => {
        console.log('Rate updated successfully:', updatedRate);
        alert('Rate updated successfully');
        this.router.navigate(['/admin-dashboard/list-rate']);
      },
      error: (error) => {
        console.error('Error updating rate:', error);
        alert(error.message || 'Error updating rate. Please try again.');
      }
    });
  }
}
