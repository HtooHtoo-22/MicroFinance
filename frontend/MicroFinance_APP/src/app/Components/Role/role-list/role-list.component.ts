import { Component, OnInit } from '@angular/core';
import { RoleService } from '../../../service/role.service';
import { Role } from '../../../model/Role';

@Component({
  selector: 'app-list-role',
  standalone: false,
  templateUrl: './role-list.component.html',
  styleUrls: ['./role-list.component.css']
  
})
export class ListRoleComponent implements OnInit {
  roles: Role[] = [];

  constructor(private roleService: RoleService) { }

  ngOnInit(): void {
    this.roleService.getAllRoles().subscribe(data => {
     
      this.roles = data;

      // this.roles = data.map((role: any, index: number) => ({
      //   id: role.id || index + 1,
      //   ...role
      // }));
    });
  }
}
