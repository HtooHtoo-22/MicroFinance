import { Component, OnInit } from '@angular/core';
import { RateService } from '../../../service/rate.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-rate',
  templateUrl: './create-rate.component.html',
  standalone: false,
  styleUrl: './create-rate.component.css'
})
export class CreateRateComponent implements OnInit {
  rateForm!: FormGroup;

  constructor(
    private rateService: RateService, 
    private router: Router,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.rateForm = this.fb.group({
      rateType: ['', [Validators.required]],
      value: [0, [Validators.required, Validators.min(0)]],
      status: [false]
    });
  }

  createRate(): void {
    if (this.rateForm.valid) {
      this.rateService.createRate(this.rateForm.value).subscribe({
        next: (createdRate) => {
          alert('Rate created successfully.');
          this.rateForm.reset({
            rateType: '',
            value: 0,
            status: false
          });
        },
        error: (error) => {
          console.error('Error creating rate', error);
          alert('Error creating rate.');
        }
      });
    }
  }
  
}
