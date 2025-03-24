import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ProductService } from '../../../service/product.service';
import { Product } from '../../../model/Product';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';
import { DealerService } from '../../../service/dealer.service';

@Component({
  selector: 'app-allproduct-list',
  standalone: false,
  templateUrl: './allproduct-list.component.html',
  styleUrl: './allproduct-list.component.css'
})
export class AllproductListComponent implements OnInit {
  products: Product[] = [];
  error: string | null = null;
  dealerMap: { [key: number]: string } = {}; // Map to store dealerId to businessName

  constructor(
    private productService: ProductService,
    private dealerservice: DealerService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
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
        this.dealerservice.getDealerById(product.dealerId).subscribe({
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

  selectProduct(product: any) {
    this.productService.setSelectedProduct(product);
    this.router.navigate(['operation-dashboard/hp-register']); 
  }
}