// interfaces/user.ts
export interface UserDTO {
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
    id: number;
    userId: string;
    name: string;
    email: string;
    branchName: string;
    roleName: string;
    createDate: string;
  }