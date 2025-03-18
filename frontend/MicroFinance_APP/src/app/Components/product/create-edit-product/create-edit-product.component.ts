import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../../service/product.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-create-edit-product',
  standalone: false,
  templateUrl: './create-edit-product.component.html',
  styleUrls: ['./create-edit-product.component.css']
})
export class CreateEditProductComponent implements OnInit {
  productForm!: FormGroup;
  isEditing: boolean = false;
  productId: number | null = null;
  selectedFile: File | null = null;
  previewImage: string | null = null;
  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.productForm = this.fb.group({
      productName: ['', Validators.required],
      value: ['', Validators.required],
      dealerRegisterId: ['', Validators.required],
      status: [false]
    });
  
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      console.log("Product ID from URL:", id); // Check if undefined or NaN
  
      if (id) {
        this.isEditing = true;
        this.productId = +id;
        this.loadProductData(this.productId);
      }
    });
  }
  

  // Load Product Data if Editing
  loadProductData(id: number): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe(
      (product) => {
        console.log('Product:', product);
        this.productForm.patchValue({
          productName: product.productName,
          value: product.value,
          dealerRegisterId: product.dealerRegisterId,
          status: product.status
        });

        if (product.photo) {
          this.previewImage = typeof product.photo === 'string' ? product.photo : null;
        }

        this.loading = false;
      },
      (error) => {
        console.error('Error loading product:', error);
        this.errorMessage = 'Error loading product data!';
        this.loading = false;
      }
    );
  }

  // Handle Image Upload
  onFileSelected(event: any): void {
    if (event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.previewImage = e.target.result;
      };
      if (this.selectedFile) {
        reader.readAsDataURL(this.selectedFile);
      }
    }
  }

  // Submit Form
  onSubmit(): void {
    if (this.productForm.invalid) {
      alert('Please fill all required fields.');
      return;
    }
  
    // Convert form values to a JSON object
    const productData = {
      productName: this.productForm.value.productName,
      value: this.productForm.value.value,
      dealerRegisterId: this.productForm.value.dealerRegisterId,
      status: this.productForm.value.status === 'true' ? true : false
    };
    
  
  
    const formData = new FormData();
    formData.append('product', JSON.stringify(productData)); // Append JSON data as a string
  
    if (this.selectedFile) {
      formData.append('photo', this.selectedFile); // Append file if available
    }
  
    this.loading = true;
    this.successMessage = null;
    this.errorMessage = null;
  
    if (this.isEditing && this.productId) {
      this.productService.updateProduct(this.productId!, productData, this.selectedFile!).subscribe(
        () => {
          this.successMessage = 'Product updated successfully!';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/products']), 2000);
        },
        (error) => {
          this.errorMessage = 'Failed to update product. Please try again.';
          console.error(error);
          this.loading = false;
        }
      );
      
    } else {
      // Create new product
      this.productService.createProduct(productData, this.selectedFile!).subscribe(
        () => {
          this.successMessage = 'Product created successfully!';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/products']), 2000);
        },
        (error) => {
          this.errorMessage = 'Failed to create product. Please try again.';
          console.error(error);
          this.loading = false;
        }
      );
    }
  }
  
}
