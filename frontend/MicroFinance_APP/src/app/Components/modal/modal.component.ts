import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modal',
  standalone: false,
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.css'
})
export class ModalComponent {
  message: string;
  success: boolean;
  constructor(
    public dialogRef: MatDialogRef<ModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string; success: boolean }
  ) {
    this.message = data.message; // Store data in properties
    this.success = data.success;
  }

  close(): void {
    this.dialogRef.close();
  }
}
