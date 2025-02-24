import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-collateral-type',
  standalone: false,
  templateUrl: './collateral-type.component.html',
  styleUrl: './collateral-type.component.css'
})
export class CollateralTypeComponent implements OnInit {
  collateralTypeForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.collateralTypeForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(30)]]
    });
  }

  onSubmit(): void {
    if (this.collateralTypeForm.valid) {
      console.log('Collateral Type Form Submitted:', this.collateralTypeForm.value);
      // Add your API call here to save the collateral type
    }
  }
}
