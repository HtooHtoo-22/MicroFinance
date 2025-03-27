import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentAccService } from '../../../service/current-acc.service'; // Adjust the path as necessary
import { CurrentAccount } from '../../../model/CurrentAcc';
import { Cif } from '../../../model/CIF';
import { CifService } from '../../../service/cif.service';
import { ModelComponent } from '../../model/model.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-current-account-register',
  standalone: false,
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateComponent implements OnInit {
  currentAccountForm!: FormGroup;
  cifs: Cif[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private currentAccService: CurrentAccService, // Inject the service
    private cifService: CifService // Inject the CIF service
  ) {}

  ngOnInit(): void {
    this.cifService.listCif().subscribe({
      next: (response) => {
        console.log('CIFs loaded:', response); // Add logging
        this.cifs = response.data || [];
      },
      error: (error) => {
        console.error('Error fetching CIFs:', error);
        alert('Failed to load CIFs');
      }
    });

    this.currentAccountForm = this.fb.group({
      cifId: ['', Validators.required], // CIF ID is required
      cifCode: [''], // Optional CIF code
      maxAmount: ['', [Validators.required, Validators.min(0)]], // Max amount is required
      minAmount: ['', [Validators.required, Validators.min(0)]], // Min amount is required
      totalBalance: [0], // Default value for total balance
      freezeStatus: [false], // Default value for freeze status
      accountId: [''] // Optional, can be generated on the backend
    });

    this.route.params.subscribe(params => {
      const cifId = params['cifId'];


      if (cifId) {
        this.currentAccountForm.patchValue({ cifId: cifId });

        // ✅ Fetch CIF details to get `cifCode`
        this.cifService.getCifById(cifId).subscribe({
          next: (cifData) => {
            console.log('CIF API response:', cifData);
            // Now check if cifData.data exists
            if (cifData && cifData.data) {
              this.currentAccountForm.patchValue({ cifCode: cifData.data.cifId });
            } else if (cifData && cifData.cifId) {
              // If the API returns the cif object directly (without a wrapper)
              this.currentAccountForm.patchValue({ cifCode: cifData.cifId });
            } else {
              console.warn('No CIF data found in the API response');
            }
          },
          error: (error) => {
            console.error('Error fetching CIF details:', error);
          }
        });

      }

    });
  }

  onSubmit() {
    if (this.currentAccountForm.valid) {
      const formValue = this.currentAccountForm.value;

      // Convert cifId to a number and ensure it's valid
      const cifId = Number(formValue.cifId);
      const cifCode = formValue.cifCode;
      if (isNaN(cifId)) {
        this.showModal('Invalid CIF ID', false);
          console.error('CIF ID is not a valid number');

          return;
      }



      const accountData: CurrentAccount = {
          id: 0, // ID will be assigned by the backend
          accountId: '',
          maxAmount: Number(formValue.maxAmount),
          minAmount: Number(formValue.minAmount),
          cifId: cifId,
          cifCode: cifCode,
          totalBalance: 0,
          freezeStatus: false,
          createDate: '',
          userName: ''
      };


        this.currentAccService.createCurrentAccount(accountData).subscribe({
            next: (response) => {
              if (response.data) {
                console.log('Current Account created:', response);
                this.currentAccountForm.patchValue({
                    cifCode: response.data.cifCode  // ✅ Assign received cifCode to form
                });
                this.showModal('Current account created successfully.', true);
                this.router.navigate(['/entry-dashboard/current-acc-list']); // Navigate after setting
            } // Navigate to the list after creation
            },
            error: (error) => {
                console.error('Error creating Current Account:', error);
                this.showModal('Error creating current account. Please try again.', false);
            }
        });
    } else {
        console.log('Form is invalid');
    }
}

showModal(message: string, success: boolean): void {
    this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success, }
    });
  }


}
