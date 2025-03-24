// edit-role.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleService } from '../../../service/role.service';
import { PermissionService } from '../../../service/permission-service.service';

@Component({
  selector: 'app-edit-role',
  standalone: false,
  templateUrl: './edit-role.component.html',
  styleUrls: ['./edit-role.component.css']
})
export class EditRoleComponent implements OnInit {
  roleId!: number;
  role: any = {};
  allPermissions: string[] = [];
  selectedPermissions: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private roleService: RoleService,
    private permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.roleId = +idParam;
        this.loadRole(this.roleId);
        this.loadPermissions();
      }
    });
  }

  loadRole(id: number) {
    this.roleService.getRoleById(id).subscribe({
      next: (data: any) => {
        this.role = data;
        this.selectedPermissions = data.permissions;
      }
    });
  }

  loadPermissions() {
    this.permissionService.getAllPermissions().subscribe({
      next: (permissions) => {
        this.allPermissions = permissions;
      }
    });
  }

  togglePermission(permission: string) {
    const index = this.selectedPermissions.indexOf(permission);
    if (index === -1) {
      this.selectedPermissions.push(permission);
    } else {
      this.selectedPermissions.splice(index, 1);
    }
  }

  updateRole() {
    const roleData = {
      ...this.role,
      permissions: this.selectedPermissions
    };
    
    this.roleService.updateRole(this.roleId, roleData).subscribe({
      next: () => {
        alert('Role updated successfully!');
        this.router.navigate(['/list-role']);
      }
    });
  }
}