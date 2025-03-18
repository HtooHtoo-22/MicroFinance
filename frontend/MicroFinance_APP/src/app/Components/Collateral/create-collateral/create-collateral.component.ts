import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-collateral',
  standalone: false,
  templateUrl: './create-collateral.component.html',
  styleUrl: './create-collateral.component.css'
})
export class CreateCollateralComponent implements OnInit {
  collateralForm!: FormGroup;
  collateralTypes: any[] = []; // This should be populated from your backend
  selectedFile: File | null = null; 

  constructor(
    private fb: FormBuilder,
    // Add your service here: private collateralService: CollateralService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCollateralTypes();
  }

  private initForm(): void {
    this.collateralForm = this.fb.group({
      value: [null, [Validators.required, Validators.min(0)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      status: [true, Validators.required],
      address: ['', [Validators.required, Validators.maxLength(200)]],
      image: ['', [Validators.required, Validators.maxLength(255)]],
      collateralType: [null, Validators.required],
    });
  }

  private loadCollateralTypes(): void {
    // Temporarily using mock data - replace with actual API call
    this.collateralTypes = [
      { id: 1, name: 'Real Estate' },
      { id: 2, name: 'Vehicle' },
      { id: 3, name: 'Jewelry' },
      { id: 4, name: 'Equipment' }
    ];
    // When you have a service:
    // this.collateralService.getCollateralTypes().subscribe(
    //   types => this.collateralTypes = types
    // );
  }

  onFileChange(event: any, fieldName: string): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      
      // Create a FileReader to read the file
      const reader = new FileReader();
      reader.onload = (e: any) => {
        // Update the form control with the file's data URL
        this.collateralForm.patchValue({
          image: e.target.result
        });
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.collateralForm.valid) {
      console.log('Collateral Form Submitted:', this.collateralForm.value);
      // Add your API call here to save the collateral
    } else {
      this.markFormGroupTouched(this.collateralForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}