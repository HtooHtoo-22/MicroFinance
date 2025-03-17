import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ProductService } from '../../../service/product.service';
import { Product } from '../../../model/Product';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-allproduct-list',
  standalone: false,
  templateUrl: './allproduct-list.component.html',
  styleUrl: './allproduct-list.component.css'
})
export class AllproductListComponent implements OnInit {
  products: Product[] = [];
  error: string | null = null;

  constructor(
    private productService: ProductService,
    private authService: AuthService, // Add AuthService
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {
    console.log('AuthService instance:', this.authService);
  console.log('getCurrentDealerId exists:', typeof this.authService.getCurrentDealerId === 'function');
  }

  ngOnInit(): void {
    const dealerId = this.authService.getCurrentDealerId();
    console.log('Dealer ID:', dealerId);
  
    if (dealerId) {
      this.productService.getProductsByDealerId(dealerId)
        .subscribe({
          next: (products) => {
            console.log('Received products:', products);
            this.products = products;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error in component:', err);
            this.error = 'Failed to load products';
            this.cdr.detectChanges();
          }
        });
    } else {
      this.error = 'No dealer ID found. Please login as a dealer.';
      this.cdr.detectChanges();
    }
  }

  selectProduct(product: any) {
    this.productService.setSelectedProduct(product);
    this.router.navigate(['dashboard/hp-register']); 
  }
}