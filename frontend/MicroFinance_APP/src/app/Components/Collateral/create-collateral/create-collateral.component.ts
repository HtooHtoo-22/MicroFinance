import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CollateralService } from '../../../service/collateral.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { CollateralDTO } from '../../../model/CollateralDTO';

@Component({
  selector: 'app-create-collateral',
  standalone: false,
  templateUrl: './create-collateral.component.html',
  styleUrl: './create-collateral.component.css'
})
export class CreateCollateralComponent implements OnInit {
  collateralForm!: FormGroup;
  collateralTypes: any[] = []; // This should be populated from your backend
  accountList: any[] = [];
  selectedFile: File | null = null; 
  collateralDTO !: CollateralDTO ;
  message :string = '';
  error : boolean = false;
  constructor(
    private collateralService: CollateralService,
    private currentAccService: CurrentAccService,
    private fb: FormBuilder,
    
    
    // Add your service here: private collateralService: CollateralService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCollateralTypes();
    this.loadCurrentAccounts();
  }

  private initForm(): void {
    this.collateralForm = this.fb.group({
      currentAccountId: [null, Validators.required],
      value: [null, [Validators.required, Validators.min(0)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      status: [true, Validators.required],
      address: ['', [Validators.required, Validators.maxLength(200)]],
      //image: ['', [Validators.required, Validators.maxLength(255)]],
      collateralTypeId: [null, Validators.required],
    });
  }

  private loadCollateralTypes(): void {
    this.collateralService.getAllCollateralTypes().subscribe({
      next: (types) => {
        this.collateralTypes = types;
        console.log('Collateral Types:', this.collateralTypes);
        
      },
      error: (err) => {
        console.error('Error fetching collateral types:', err);
      }
    });
  }
  private loadCurrentAccounts(): void {
    this.currentAccService.listCurrentAcc().subscribe({
      next: (accounts) => {
        this.accountList = accounts.data;
        console.log('Current Accounts:', this.accountList);
        
      },
      error: (err) => {
        console.error('Error fetching Current Account types:', err);
      }
    });
  }
  

  onFileChange(event: any): void {
    if (event.target.files.length > 0) {
      this.selectedFile = event.target.files[0]; // Store selected file
    }
  }

  onSubmit(): void {
    console.log("Form submitted");
  
    if (this.collateralForm.valid) {
      console.log("Form values:", this.collateralForm.value);
      this.collateralDTO = {
        ...this.collateralForm.value,
        imageFile: this.selectedFile || undefined // Attach the selected file
      };
  
      console.log("Final DTO:", this.collateralDTO);
      this.collateralService.createCollateral(this.collateralDTO).subscribe({
        next: (response) => {
          if (response && response.message) {  // Assuming `success` is a boolean in ApiResponse
            console.log("Collateral created successfully:", response.message);
            this.message = response.message;
            this.collateralForm.reset(); // Reset form after success
          } else {
            console.error("Error:", response.message);
           
          }
        },
        error: (error) => {
          console.error("API Error:", error);
          this.message = error.message;
          error = true;
        }
      });
      
        
    } else {
      // Print errors to the console
      Object.keys(this.collateralForm.controls).forEach(control => {
        const formControl = this.collateralForm.get(control);
        if (formControl?.invalid) {
          console.log(`Error in ${control}:`, formControl.errors);
        }
      });
  
      this.markFormGroupTouched(this.collateralForm);
      console.log("Form is invalid");
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