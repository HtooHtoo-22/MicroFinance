export interface Product {
  id?: number;
  productName: string;
  value: number;
  photo?: string; // Backend should return URL string
  status: boolean;
  dealerId?: number; // Include dealerId
}