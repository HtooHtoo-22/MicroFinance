import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  standalone: false,
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.css'
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
minAmount: string|number;
maxAmount: string|number;
totalBalance: string|number;
userName: String;
accountId: String;
 message: string 
}
  ) {}

  confirm(): void {
    this.dialogRef.close(true); // User confirmed
  }

  cancel(): void {
    this.dialogRef.close(false); // User canceled
  }
}
