import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Product } from '../model/Product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private selectedProduct: any;

  private apiUrl = 'http://localhost:8081/api/products'; 
  constructor(private http:HttpClient) { }

  createProduct(product: Product,photo: File ): Observable<any>{
    const formData = new FormData();
    formData.append('product', JSON.stringify(product));
    formData.append('userPhoto',photo);

    return this.http.post<any>(`${this.apiUrl}`,formData);
   
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data) // Extract `data`
    );
  }
  
  
  updateProduct(id: number, productData: any, photo: File | null): Observable<any> {
    const formData = new FormData();

    // Remove 'id' from productData before sending
    const { id: _, ...productWithoutId } = productData; 
    
    formData.append('product', JSON.stringify(productWithoutId)); // Send product data without id

    if (photo) {
      formData.append('photo', photo); // Append file only if it exists
    }

    console.log("Sending update request:", formData.get('product')); // Log product data
    console.log("Sending file:", formData.get('photo')); // Corrected log key

    return this.http.put<any>(`${this.apiUrl}/${id}`, formData);

}


  
  getProducts(): Observable<Product[]> {
    return this.http.get<any>(`${this.apiUrl}/dealer/1`).pipe(
      map(response => response.data) // Assuming APIResponse format
    );
  }

  setSelectedProduct(product: any) {
    this.selectedProduct = product;
  }

  getSelectedProduct() {
    return this.selectedProduct;
  }

  clearSelectedProduct() {
    this.selectedProduct = null; // Reset selection after use
  }
}
