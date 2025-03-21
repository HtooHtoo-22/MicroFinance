import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CollateralService } from '../../../service/collateral.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { CollateralDTO } from '../../../model/CollateralDTO';
import { ModelComponent } from '../../model/model.component';
import { MatDialog } from '@angular/material/dialog';

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
  filteredAccounts: any[] = [];
  constructor(
    private collateralService: CollateralService,
    private currentAccService: CurrentAccService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    
    
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
    currentAccountDisplay: ['', Validators.required],
      value: [null, [Validators.required, Validators.min(0)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      status: [true, Validators.required],
      address: ['', [Validators.required, Validators.maxLength(200)]],
      //image: ['', [Validators.required, Validators.maxLength(255)]],
      collateralTypeId: [null, Validators.required],
      name: ['', [Validators.required, Validators.maxLength(30)]]

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
        this.showModal('Failed to fetch collateral types', false
        );
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
        this.showModal('Failed to fetch current accounts', false);
      }
    });
  }
  onAccountInputChange(event: Event): void {
    const input = event.target as HTMLInputElement; // Cast to HTMLInputElement
    const value = input.value; // Now you can safely access the value
  
    if (!value) {
      this.filteredAccounts = [];
      return;
    }
  
    const lowerCaseValue = value.toLowerCase();
    this.filteredAccounts = this.accountList.filter(account =>
      account.accountId.toLowerCase().includes(lowerCaseValue)
    );
  }
  // selectAccount(account: any): void {
  //   currentAccountId: account.id;
  //   const currentAccountControl = this.collateralForm.get('currentAccountId');
  //   if (currentAccountControl) {
  //     currentAccountControl.setValue(account.accountId);
  //   }
  //   this.filteredAccounts = []; // Clear the suggestions
  // }

  selectAccount(account: any): void {
    this.collateralForm.patchValue({
      currentAccountDisplay: account.accountId, // Show string accountId in UI
      currentAccountId: account.id  // Use integer id for backend submission
    });
    this.filteredAccounts = [];
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

      delete this.collateralForm.value.currentAccountDisplay; 
      this.collateralForm.value.currentAccountId = Number(this.collateralForm.value.currentAccountId);
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
            this.showModal('Failed to create collateral', false);
           
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
      this.showModal('Failed to create collateral', false);
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

  showModal(message: string, success: boolean): void {
          this.dialog.open(ModelComponent, {
            width: '300px',
            data: { message, success, }
          });
        }
}