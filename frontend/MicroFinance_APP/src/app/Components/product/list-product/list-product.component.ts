import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../service/product.service';
import { Product } from '../../../model/Product';
import { ActivatedRoute, Router } from '@angular/router';


@Component({
  selector: 'app-list-product',
  standalone: false,
  templateUrl: './list-product.component.html',
  styleUrl: './list-product.component.css'
})
export class ListProductComponent implements OnInit {
  products: Product[] = [];
  

  constructor(private productService: ProductService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts():void{
    this.productService.getProducts().subscribe(
      (data) => {
       this.products = data;
       console.log('Products:', data);
      },
      (error) => {
        console.error('Error fetching products:', error);
      }
    );
  }



  selectProduct(product: any) {
    this.productService.setSelectedProduct(product);
    this.router.navigate(['dashboard/hp-loan-register']); 
  }

}
