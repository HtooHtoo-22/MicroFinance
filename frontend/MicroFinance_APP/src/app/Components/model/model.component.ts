import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-model',
  standalone: false,
  templateUrl: './model.component.html',
  styleUrl: './model.component.css'
})
export class ModelComponent {
  message: string;
  success: boolean;
  constructor(
    public dialogRef: MatDialogRef<ModelComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string; success: boolean }
  ) {
    this.message = data.message; // Store data in properties
    this.success = data.success;
  }

  close(): void {
    this.dialogRef.close();
  }
}

