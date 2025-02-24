export interface Branch {
   
    id?: number;
    code:string; // Make id optional
    name: string;
    address: string;
    state: string;
    township: string;
    status: string;
    created_at: Date;
    updated_at: Date;
  }
  