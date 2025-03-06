import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentAccService } from '../../../service/current-acc.service';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../../model/model.component';
@Component({
  selector: 'app-update',
  standalone: false,
  templateUrl: './update.component.html',
  styleUrl: './update.component.css'
})
export class UpdateComponent implements OnInit {
  currentAccountForm!: FormGroup;
  accountId!: string;
  cifId: number | undefined;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private dialog:MatDialog,
    private currentAccService: CurrentAccService
  ) {}

  ngOnInit(): void {
    this.accountId = this.route.snapshot.params['accountId'];
    this.initializeForm();
    this.loadAccountData();
  }

  initializeForm(): void {
    this.currentAccountForm = this.fb.group({
      maxAmount: ['', [Validators.required, Validators.min(0)]],
      minAmount: ['', [Validators.required, Validators.min(0)]],
      totalBalance: ['', [Validators.required, Validators.min(0)]],
      freezeStatus: [false]
    });
  }

  loadAccountData(): void {
    this.currentAccService.getCurrentAccountById(this.accountId).subscribe({
      next: (response) => {
        const account = response.data;
        this.currentAccountForm.patchValue({
          maxAmount: account.maxAmount,
          minAmount: account.minAmount,
          freezeStatus: account.freezeStatus
        });

        this.accountId = account.accountId;
        this.cifId = account.cifId;
      },
      error: (error) => {
        console.error('Error loading account:', error);
        alert('Failed to load account details.');
      }
    });
  }

  showModal(message: string, success: boolean): void {
    this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success, }
    });
  }

  onSubmit(): void {
    if (this.currentAccountForm.valid) {
      const formData = this.currentAccountForm.value;
      const accountData: CurrentAccount = {
        accountId: this.accountId,
        maxAmount: formData.maxAmount,
        minAmount: formData.minAmount,
        freezeStatus: formData.freezeStatus,
        // Include other required fields (not editable)
        cifId: this.cifId ?? 0, // Placeholder (not used in update)
        createDate: '', // Placeholder
        totalBalance: formData.totalBalance,
        userName: '',
       
      };

      this.currentAccService.updateCurrentAccount(this.accountId, accountData).subscribe({
        next: (response) => {
          this.showModal('Account updated successfully!',true);
          this.router.navigate(['/dashboard/current-acc-list']);
        },
        error: (error) => {
          console.error('Error updating account:', error);
          this.showModal('Failed to update account.', false);
        }
      });
    }
  }
}