import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleService } from '../../../service/role.service';
import { Role } from '../../../model/Role';

@Component({
  selector: 'app-edit-role',
  standalone: false,
  templateUrl: './edit-role.component.html',
  styleUrls: ['./edit-role.component.css']
})
export class EditRoleComponent implements OnInit {
  roleId!: number;
  role: Role = {
    roleName: '',
    roleDescription: '',
    active: false,
    id: 0
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private roleService: RoleService
  ) {}

  ngOnInit(): void {
    // Retrieve the role id from route parameters
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.roleId = +idParam;
        this.loadRole(this.roleId);
      } else {
        alert('No role id provided.');
        // Optionally navigate back to list
        this.router.navigate(['/list-role']);
      }
    });
  }

  loadRole(id: number) {
    this.roleService.getRoleById(id).subscribe({
      next: (data: Role) => {
        if (data) {
          this.role = data;
        } else {
          alert('Role not found.');
          this.router.navigate(['/list-role']);
        }
      },
      error: (error) => {
        console.error('Error retrieving role:', error);
        alert('Error retrieving role.');
      }
    });
  }

  updateRole() {
    this.roleService.updateRole(this.roleId, this.role).subscribe({
      next: (updatedRole: Role) => {
        alert('Role updated successfully.');
        // Optionally, navigate to the list page:
        // this.router.navigate(['/list-role']);
      },
      error: (error) => {
        console.error('Error updating role:', error);
        alert('Error updating role. Please try again.');
      }
    });
  }
}
