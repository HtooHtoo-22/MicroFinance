import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';  // Import FormsModule here

import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { CreateBranchComponent } from './Components/branch/create-branch/create-branch.component';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { importProvidersFrom } from '@angular/core';
import { ListBranchComponent } from './Components/branch/list-branch/list-branch.component';
import { UpdateBranchComponent } from './Components/branch/update-branch/update-branch.component';
import { ListProductComponent } from './Components/product/list-product/list-product.component';
import { CreateProductComponent } from './Components/product/create-product/create-product.component';
import { EditProductComponent } from './Components/product/edit-product/edit-product.component';


@NgModule({
  declarations: [
    AppComponent,
    ListBranchComponent,
    CreateBranchComponent,
    UpdateBranchComponent,
    ListProductComponent,
    CreateProductComponent,
    EditProductComponent
  
  ],
  imports: [
    BrowserModule,
    RouterModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    CommonModule
],
  providers: [
    importProvidersFrom(ListBranchComponent)
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
