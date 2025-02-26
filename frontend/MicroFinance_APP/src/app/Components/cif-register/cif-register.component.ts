import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-cif-register',
  standalone: false,
  templateUrl: './cif-register.component.html',
  styleUrl: './cif-register.component.css'
})
export class CifRegisterComponent {
  cifForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.cifForm = this.fb.group({
      userName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['+95 ', Validators.required],
      gender: ['', Validators.required],
      state: ['', Validators.required],
      township: ['', Validators.required],
      address: ['', Validators.required],
      NRC: ['', Validators.required],
      frontNRCPhoto: [null],
      backNRCPhoto: [null],
      userPhoto: [null]
    });
  }

  onFileChange(event: any, controlName: string) {
    const file = event.target.files[0];
    if (file) {
      this.cifForm.get(controlName)?.setValue(file);
    }
  }

  onSubmit() {
    console.log(this.cifForm.value);
  }

}
