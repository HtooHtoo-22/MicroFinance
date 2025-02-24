import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './Components/dashboard/dashboard.component';
import { CifRegisterComponent } from './Components/cif-register/cif-register.component';
import { TransactionComponent } from './Components/transaction/transaction.component';
import { TransactionHistoryComponent } from './Components/transaction-history/transaction-history.component';
import { CurrentAccListComponent } from './Components/current-acc-list/current-acc-list.component';
import { HomeComponent } from './Components/home/home.component';
import { CurrentAccountRegisterComponent } from './Components/current-account-register/current-account-register.component';
import { SmeLaonRegisterComponent } from './Components/sme-laon-register/sme-laon-register.component';
import { CollateralComponent } from './Components/collateral/collateral.component';
import { SmeLoanListComponent } from './Components/sme-loan-list/sme-loan-list.component';
import { CommonModule, NgClass, NgFor } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CollateralTypeComponent } from './Components/collateral-type/collateral-type.component';
import { LoginComponent } from './Components/login/login.component';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { CreateuserComponent } from './Components/user/createuser/createuser.component';
import { ListuserComponent } from './Components/user/listuser/listuser.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    CifRegisterComponent,
    CurrentAccListComponent,
    TransactionComponent,
    TransactionHistoryComponent,
    CurrentAccountRegisterComponent,
    HomeComponent,
    SmeLaonRegisterComponent,
    CollateralComponent,
    SmeLoanListComponent,
    CollateralTypeComponent,
    LoginComponent,
    ListuserComponent,
    CreateuserComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    RouterModule,
    NgClass,
    NgFor,
    HttpClientModule,
],
  providers: [
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
