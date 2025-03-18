export interface Product {
    
    id?: number;
    productName: string;
    value: number;
    photo?: File | string;
    dealerRegisterId: number;
    status: boolean;
  }
  