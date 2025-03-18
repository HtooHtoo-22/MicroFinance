import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './Components/dashboard/dashboard.component';
import { TransactionComponent } from './Components/transaction/transaction.component';
import { TransactionHistoryComponent } from './Components/transaction-history/transaction-history.component';
import { CurrentAccListComponent } from './Components/current-acc-list/current-acc-list.component';
import { CommonModule, NgClass, NgFor } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LoginComponent } from './Components/login/login.component';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { CreateuserComponent } from './Components/user/createuser/createuser.component';
import { ListuserComponent } from './Components/user/listuser/listuser.component';
import { CurrentAccountRegisterComponent } from './Components/current-acc-register/current-acc-register.component';
import { SmeLoanRegisterComponent } from './Components/sme-loan-register/sme-loan-register.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { SmeLaonHistoryComponent } from './Components/sme-laon-history/sme-laon-history.component';
import { CreateBranchComponent } from './Components/branch/create-branch/create-branch.component';
import { BranchListComponent } from './Components/branch/branch-list/branch-list.component';
import { UpdateBranchComponent } from './Components/branch/update-branch/update-branch.component';
import { UpdateUserComponent } from './Components/user/update-user/update-user.component';
import { CreateRoleComponent } from './Components/Role/create-role/create-role.component';
import { EditRoleComponent } from './Components/Role/edit-role/edit-role.component';
import { ListRoleComponent } from './Components/Role/role-list/role-list.component';
import { CreateRateComponent } from './Components/Rate/create-rate/create-rate.component';
import { RateListComponent } from './Components/Rate/rate-list/rate-list.component';
import { UpdateRateComponent } from './Components/Rate/update-rate/update-rate.component';
import { CreateCollateralTypeComponent } from './Components/collateral_Type/create-collateral-type/create-collateral-type.component';
import { CollateralTypeListComponent } from './Components/collateral_Type/collateral-type-list/collateral-type-list.component';
import { CollateralTypeUpdateComponent } from './Components/collateral_Type/collateral-type-update/collateral-type-update.component';
import { CreateCollateralComponent } from './Components/Collateral/create-collateral/create-collateral.component';
import { CreateCifComponent } from './Components/CIF/create-cif/create-cif.component';
import { CifListComponent } from './Components/CIF/cif-list/cif-list.component';
import { CreateComponent } from './Components/CurrentACC/create/create.component';
import { ListComponent } from './Components/CurrentACC/list/list.component';
import { UpdateComponent } from './Components/CurrentACC/update/update.component';
import { CustomerDetailComponent } from './Components/Account/customer-detail/customer-detail.component';
import { DealerListComponent } from './Components/Dealer/dealer-list/dealer-list.component';
import { CreateDealerComponent } from './Components/Dealer/create-dealer/create-dealer.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ModelComponent } from './Components/model/model.component';
import { ConfirmDialogComponent } from './Components/confirm-dialog/confirm-dialog.component';
import { BranchAccountCountComponent } from './Components/counting/branch-account-count/branch-account-count.component';
import { BranchUserCountComponent } from './Components/counting/branch-user-count/branch-user-count.component';
import { CifDetailComponent } from './Components/CIF/cif-detail/cif-detail.component';
import { DealerDetailComponent } from './Components/Account/dealer-detail/dealer-detail.component';
import { DealerDashboardComponent } from './Components/dealer-dashboard/dealer-dashboard.component';
import { DealerTransactionComponent } from './Components/Dealer/dealer-transaction/dealer-transaction.component';
import { HpLoanListComponent } from './Components/hp-loan/hp-loan-list/hp-loan-list.component';
import { HpLoanFormComponent } from './Components/hp-loan/hp-loan-form/hp-loan-form.component';
import { ListProductComponent } from './Components/product/list-product/list-product.component';
import { CreateEditProductComponent } from './Components/product/create-edit-product/create-edit-product.component';


@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    CurrentAccListComponent,
    TransactionComponent,
    TransactionHistoryComponent,
    CurrentAccountRegisterComponent,
    LoginComponent,
    ListuserComponent,
    CreateuserComponent,
    SmeLaonHistoryComponent,
    SmeLoanRegisterComponent,
    CreateBranchComponent,
    BranchListComponent,
    UpdateBranchComponent,
    UpdateUserComponent,
    CreateRoleComponent,
    EditRoleComponent,
    ListRoleComponent,
    CreateRateComponent,
    RateListComponent,
    UpdateRateComponent,
    CreateCollateralTypeComponent,
    CollateralTypeListComponent,
    CollateralTypeUpdateComponent,
    CreateCollateralComponent,
    CreateCifComponent,
    CifListComponent,
    CreateComponent,
    ListComponent,
    UpdateComponent,
    CustomerDetailComponent,
    CreateDealerComponent,
    DealerListComponent,
    ModelComponent,
    ConfirmDialogComponent,
    BranchAccountCountComponent,
    BranchUserCountComponent,
    CifDetailComponent,
    DealerDetailComponent,
    DealerDashboardComponent,
    DealerTransactionComponent,
    HpLoanListComponent,
    HpLoanFormComponent,
    ListProductComponent,
    CreateEditProductComponent,
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
    MatSnackBarModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule
  ],
  providers: [
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
