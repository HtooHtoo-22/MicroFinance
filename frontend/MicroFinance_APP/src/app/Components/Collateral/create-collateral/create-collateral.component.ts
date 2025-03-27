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
  collateralDTO!: CollateralDTO;
  message: string = '';
  error: boolean = false;
  filteredAccounts: any[] = [];
  collateralPhotoFile?: File;
  collateralPhotoUrl: string | ArrayBuffer | null = null;
  showSuccessModal: boolean = false;
  alertMessage: string = ''; // Declare the alertMessage property
  showAlert: boolean = false; // Declare the showAlert property



  constructor(
    private collateralService: CollateralService,
    private currentAccService: CurrentAccService,
    private fb: FormBuilder,
    private dialog: MatDialog
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
        this.showModal('Failed to fetch collateral types', false);
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

  selectAccount(account: any): void {
    this.collateralForm.patchValue({
      currentAccountDisplay: account.accountId, // Show string accountId in UI
      currentAccountId: account.id  // Use integer id for backend submission
    });
    this.filteredAccounts = [];
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.collateralPhotoFile = file;
        this.collateralPhotoUrl = e.target?.result ?? null;
      };
      reader.readAsDataURL(file);
    }
  }

  removeFile(): void {
    this.collateralPhotoFile = undefined;
    this.collateralPhotoUrl = null;
  }

  onSubmit(): void {
    console.log("Form submitted");
  
    if (this.collateralForm.valid) {
      console.log("Form values:", this.collateralForm.value);
  
      delete this.collateralForm.value.currentAccountDisplay; 
      this.collateralForm.value.currentAccountId = Number(this.collateralForm.value.currentAccountId);
      this.collateralDTO = {
        ...this.collateralForm.value,
        imageFile: this.collateralPhotoFile || undefined // Attach the selected file
      };
  
      console.log("Final DTO:", this.collateralDTO);
      this.collateralService.createCollateral(this.collateralDTO).subscribe({
        next: (response) => {
          if (response && response.message) {
            console.log("Collateral created successfully:", response.message);
            this.message = response.message;
            this.collateralForm.reset(); // Reset form after success
            this.removeFile(); // Reset the file
            this.showSuccessModal = true; // Show success modal
          } else {
            console.error("Error:", response.message);
            this.alertMessage = 'Failed to create collateral';
            this.showAlert = true; // Show error alert
          }
        },
        error: (error) => {
          console.error("API Error:", error);
          this.alertMessage = 'Failed to create collateral';
          this.showAlert = true; // Show error alert
        }
      });
    } else {
      console.warn("Form is invalid");
      this.markFormGroupTouched(this.collateralForm);
      this.alertMessage = 'Please fill in all required fields';
      this.showAlert = true; // Show error alert
    }
  }

// Duplicate method removed

closeModal(): void {
  this.showSuccessModal = false; // Close success modal
  this.showAlert = false; // Close error alert
}

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  private showModal(message: string, success: boolean): void {
    this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success }
    });
  }

}