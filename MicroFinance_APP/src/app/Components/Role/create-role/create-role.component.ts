import { Component } from '@angular/core';
import { RoleService } from '../../../service/role.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Role } from '../../../model/Role';

@Component({
  selector: 'app-create-role',
  templateUrl: './create-role.component.html',
  styleUrls: ['./create-role.component.css'],
  standalone: false,
})
export class CreateRoleComponent {
  roleForm: FormGroup;
  showSuccessModal: boolean = false;
  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    private router: Router
  ) { 
    this.roleForm = this.fb.group({
      roleName: [''],
      roleDescription: ['']
    });
  }

  
  createRole() {
    if (this.roleForm.valid) {
      const role: Role = this.roleForm.value;
      this.roleService.createRole(role).subscribe({
        next: () => {
          this.showSuccessModal = true;
        
          this.roleForm.reset();  // Reset the form fields
          this.router.navigate(['/list-role']); // Navigate to the role list page
        },
        error: (error) => {
          console.error('Error creating role:', error);
          alert('Error creating role. Please try again.');
        }
      });
    }
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/dashboard/list-role']);
  }
}