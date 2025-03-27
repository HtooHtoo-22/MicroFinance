import { Component, OnInit } from '@angular/core';
import { RateService } from '../../../service/rate.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ModelComponent } from '../../model/model.component';
import { MatDialog } from '@angular/material/dialog';

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
    private fb: FormBuilder,
    private dialog: MatDialog
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
          this.showModal('Rate created successfully!', true);
          this.rateForm.reset({
            rateType: '',
            value: 0,
            status: false
          });
        },
        error: (error) => {
          console.error('Error creating rate', error);
          this.showModal('Error creating rate.', false);
        }
      });
    }
  }

  showModal(message: string, success: boolean): void {
          this.dialog.open(ModelComponent, {
            width: '300px',
            data: { message, success, }
          });
        }
  
}
