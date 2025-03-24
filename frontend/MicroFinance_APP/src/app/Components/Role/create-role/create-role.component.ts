// create-role.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RoleService } from '../../../service/role.service';
import { Router } from '@angular/router';
import { PermissionService } from '../../../service/permission-service.service';

@Component({
  selector: 'app-create-role',
  standalone: false,
  templateUrl: './create-role.component.html',
  styleUrls: ['./create-role.component.css']
})
export class CreateRoleComponent implements OnInit {
  roleForm: FormGroup;
  showSuccessModal = false;
  allPermissions: string[] = [];
  selectedPermissions: string[] = [];

  constructor(
    private fb: FormBuilder,
    private roleService: RoleService,
    private permissionService: PermissionService,
    private router: Router
  ) {
    this.roleForm = this.fb.group({
      roleName: ['', Validators.required],
      roleDescription: ['', Validators.required],
      active: [true],
      permissions: [[]]
    });
  }

  ngOnInit(): void {
    this.loadPermissions();
  }

  loadPermissions() {
    this.permissionService.getAllPermissions().subscribe({
      next: (permissions) => {
        this.allPermissions = permissions;
      },
      error: (error) => console.error('Error loading permissions:', error)
    });
  }

  togglePermission(permission: string) {
    const currentPermissions = this.roleForm.get('permissions')?.value;
    const index = currentPermissions.indexOf(permission);
    
    if (index === -1) {
      currentPermissions.push(permission);
    } else {
      currentPermissions.splice(index, 1);
    }
    this.roleForm.patchValue({ permissions: currentPermissions });
  }

  createRole() {
    if (this.roleForm.valid) {
      const roleData = this.roleForm.value;
      this.roleService.createRole(roleData).subscribe({
        next: () => {
          this.showSuccessModal = true;
          this.roleForm.reset();
          this.router.navigate(['/list-role']);
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
  }
}