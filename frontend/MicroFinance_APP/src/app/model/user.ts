// interfaces/user.ts
export interface UserDTO {
    id?: number;
    name: string;
    active: boolean;
    branchId: number;
    roleId: number;
  }
  
  export interface Branch {
    id: number;
    name: string;
    code: string;
  }
  
  export interface Role {
    id: number;
    roleName: string;
  }
  
  export interface UserResponseDTO {
    active: any;
    id: number;
    userId: string;
    name: string;
    email: string;
    branchId: number;
    branchName: string;
    roleId: number;
    roleName: string;
    createDate: string;
  }