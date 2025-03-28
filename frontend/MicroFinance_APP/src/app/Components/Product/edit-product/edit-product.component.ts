import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../service/product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../../../model/Product';

@Component({
  selector: 'app-edit-product',
  standalone: false,
  templateUrl: './edit-product.component.html',
  styleUrls: ['./edit-product.component.css']
})
export class EditProductComponent implements OnInit {
  product: Product = {
    productName: '',
    value: 0,
    status: true
  };
  error: string | null = null;
  selectedFile: File | null = null;
  isLoading = false;
  showSuccessModal = false; // Success modal visibility

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.isLoading = true;
      this.productService.getProductById(Number(productId)).subscribe({
        next: (product) => {
          this.product = product;
          this.isLoading = false;
        },
        error: (err) => {
          this.error = 'Failed to load product details';
          this.isLoading = false;
          console.error(err);
        }
      });
    }
  }

  onFileChange(event: any): void {
    if (event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  onSubmit(): void {
    if (!this.product.id) {
      this.error = 'Invalid product ID';
      return;
    }

    this.isLoading = true;
    const formData = new FormData();
    formData.append('product', JSON.stringify({
      productName: this.product.productName,
      value: this.product.value,
      status: this.product.status
    }));

    if (this.selectedFile) {
      formData.append('photo', this.selectedFile);
    }

    this.productService.updateProduct(this.product.id, formData).subscribe({
      next: () => {
        this.isLoading = false;
        this.showSuccessModal = true; // Show success modal
        setTimeout(() => this.closeModal(), 3000); // Auto-close modal after 3 seconds
      },
      error: (err) => {
        this.error = 'Failed to update product';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/dealer-dashboard/all-list']); // Navigate back to the product list
  }

  goBack(): void {
    this.router.navigate(['/dealer-dashboard/all-list']);
  }
}