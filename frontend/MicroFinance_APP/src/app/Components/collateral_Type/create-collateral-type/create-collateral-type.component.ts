import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CollateralService } from '../../../service/collateral.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-collateral-type',
  standalone: false,
  templateUrl: './create-collateral-type.component.html',
  styleUrl: './create-collateral-type.component.css'
})
export class CreateCollateralTypeComponent implements OnInit {
  collateralTypeForm: FormGroup;
  showSuccessModal = false;
  showAlert = false;
  alertType = '';
  alertMessage = '';

  constructor(
    private fb: FormBuilder,
    private collateralService: CollateralService,
    private router: Router
  ) {
    this.collateralTypeForm = this.fb.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.collateralTypeForm.valid) {
      this.collateralService.createCollateralType(this.collateralTypeForm.value).subscribe({
        next: (response) => {
          this.showSuccessModal = true;
          setTimeout(() => {
            this.closeModal();
          }, 3000);
        },
        error: (error) => {
          this.alertType = 'error';
          this.alertMessage = 'Error creating collateral type';
          this.showAlert = true;
          setTimeout(() => this.showAlert = false, 3000);
        }
      });
    }
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/dashboard/collateral-type-list']);
  }
}