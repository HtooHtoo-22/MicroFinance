import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ProductService } from '../../service/product.service';
import { DealerService } from '../../service/dealer.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';
import { Product } from '../../model/Product';

@Component({
  selector: 'app-product-list',
  standalone: false,
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  error: string | null = null;
  dealerMap: { [key: number]: string } = {}; // Map to store dealerId to businessName

  constructor(
    private productService: ProductService,
    private dealerService: DealerService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    const branchId = this.authService.getCurrentUserBranchId();
    console.log('Dealer ID:', branchId);
  
    if (branchId) {
      this.productService.getProductsByBranchId(Number(branchId))
        .subscribe({
          next: (products) => {
            console.log('Received products:', products);
            this.products = products.data;
            this.fetchDealerNames();
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

  fetchDealerNames(): void {
    this.products.forEach(product => {
      if (product.dealerId) {
        this.dealerService.getDealerById(product.dealerId).subscribe({
          next: (dealer) => {
            this.dealerMap[product.dealerId!] = dealer.businessName;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error fetching dealer:', err);
          }
        });
      }
    });
  }

  editProduct(product: Product): void {
    // Navigate to edit page with product ID
    this.router.navigate(['/dealer-dashboard/products/edit', product.id]);
  }
}