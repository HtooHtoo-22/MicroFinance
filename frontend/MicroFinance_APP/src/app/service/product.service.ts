import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { Product } from '../model/Product';
import { ApiResponse } from '../model/ApiResponse';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = 'http://localhost:8081/api/products';

  constructor(private http: HttpClient) {}

  private selectedProduct: Product | null = null;


  createProduct(formData: FormData): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, formData).pipe(
      catchError(this.handleError)
    );
  }

  getProductById(productId: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${productId}`).pipe(
      catchError(this.handleError)
    );
  }

  getProductsByDealerId(dealerId: string): Observable<Product[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('access_token')}`
    });

    return this.http.get<any>(`${this.apiUrl}/dealer/${dealerId}`, { headers })
      .pipe(
        map(response => {
          console.log('Raw API Response:', response);
          console.log('Extracted data:', response.data);
          return response.data as Product[];
        }),
        catchError(this.handleError)
      );
  }

  getAllProducts(): Observable<Product[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('access_token')}`
    });

    return this.http.get<any>(`${this.apiUrl}/list`, { headers })
      .pipe(
        map(response => {
          console.log('Raw API Response:', response);
          console.log('Extracted data:', response.data);
          return response.data as Product[];
        }),
        catchError(this.handleError)
      );
  }

  updateProduct(id: number, formData: FormData): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, formData).pipe(
      catchError(this.handleError)
    );
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<never> {
    console.error('Error in ProductService:', error);
    let errorMessage = 'An error occurred while fetching products';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      errorMessage = `Server-side error: ${error.status} - ${error.error.message || error.statusText}`;
    }
    return throwError(() => new Error(errorMessage));
  }

  setSelectedProduct(product: Product) {
    this.selectedProduct = product; // Set selected product
  }

  getSelectedProduct(): Product | null {
    return this.selectedProduct; // Return selected product
  }

  clearSelectedProduct() {
    this.selectedProduct = null; // Reset selection after use
  }
}