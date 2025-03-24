// role.model.ts
export interface Role {
  id?: number;
  roleName: string;
  roleDescription: string;
  active: boolean;
  permissions: string[]; // Add permissions array
}