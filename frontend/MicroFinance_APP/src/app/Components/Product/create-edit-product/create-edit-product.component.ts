import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../../service/product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-create-edit-product',
  standalone: false,
  templateUrl: './create-edit-product.component.html',
  styleUrl: './create-edit-product.component.css'
})
export class CreateEditProductComponent implements OnInit {
  productForm!: FormGroup;
  isEditing = false;
  productId: number | null = null;
  selectedFile: File | null = null;
  previewImage: string | null = null;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  showSuccessModal: boolean = false;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.checkEditMode();
  }

  private initializeForm(): void {
    this.productForm = this.fb.group({
      productName: ['', Validators.required],
      value: ['', [Validators.required, Validators.min(0)]],
      status: [true]
    });
  }

  private checkEditMode(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditing = true;
        this.productId = +id;
        this.loadProductData(this.productId);
      }
    });
  }

  private loadProductData(productId: number): void {
    this.productService.getProductById(productId).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          productName: product.productName,
          value: product.value,
          status: product.status
        });
        this.previewImage = product.photo || null;
      },
      error: (error) => {
        this.handleError('Failed to load product data', error);
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;

      // Create preview
      const reader = new FileReader();
      reader.onload = () => {
        this.previewImage = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.markFormAsTouched();
      return;
    }

    this.loading = true;
    const productData = this.prepareProductData();

    if (this.isEditing && this.productId) {
      this.updateProduct(productData);
    } else {
      this.createProduct(productData);
    }
  }

  private prepareProductData(): FormData {
    const formData = new FormData();
    const product = {
      ...this.productForm.value,
      id: this.productId
    };

    formData.append('product', JSON.stringify(product));

    if (this.selectedFile) {
      formData.append('userPhoto', this.selectedFile);
    }

    return formData;
  }

  private createProduct(formData: FormData): void {
    this.productService.createProduct(formData).subscribe({
      next: () => {
        this.handleSuccess('Product created successfully!');
      },
      error: (error) => {
        this.handleError('Failed to create product', error);
      }
    });
  }

  private updateProduct(formData: FormData): void {
    if (!this.productId) return;

    this.productService.updateProduct(this.productId, formData).subscribe({
      next: () => {
        this.handleSuccess('Product updated successfully!');
      },
      error: (error) => {
        this.handleError('Failed to update product', error);
      }
    });
  }

  private handleSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = null;
    this.loading = false;
    this.showSuccessModal = true;
    this.productForm.reset(); // Reset the form to clear all input values
    this.previewImage = null; // Clear the preview image
    setTimeout(() => {
      this.closeModal();
      this.router.navigate(['/products']);
    }, 3000);
  }

  private handleError(message: string, error: any): void {
    console.error(error);
    this.errorMessage = `${message}: ${error.message || 'Unknown error'}`;
    this.successMessage = null;
    this.loading = false;
  }

  private markFormAsTouched(): void {
    Object.values(this.productForm.controls).forEach(control => {
      control.markAsTouched();
    });
  }

  closeModal(): void {
    this.showSuccessModal = false;
  }
}