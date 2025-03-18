import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TransactionComponent } from './Components/transaction/transaction.component';
import { LoginComponent } from './Components/login/login.component';
import { AdminDashboardComponent } from './Components/admin-dashboard/admin-dashboard.component';
import { RoleGuard } from './guards/role.guard';
import { AuthGuard } from './guards/auth.guard';
import { BranchAccountCountComponent } from './Components/counting/branch-account-count/branch-account-count.component';
import { CurrentAccountRegisterComponent } from './Components/current-acc-register/current-acc-register.component';
import { CurrentAccListComponent } from './Components/current-acc-list/current-acc-list.component';
import { TransactionHistoryComponent } from './Components/transaction-history/transaction-history.component';
import { SmeLoanRegisterComponent } from './Components/sme-loan-register/sme-loan-register.component';
import { SmeLaonHistoryComponent } from './Components/sme-laon-history/sme-laon-history.component';
import { CreateuserComponent } from './Components/user/createuser/createuser.component';
import { UpdateUserComponent } from './Components/user/update-user/update-user.component';
import { ListuserComponent } from './Components/user/listuser/listuser.component';
import { BranchListComponent } from './Components/branch/branch-list/branch-list.component';
import { CreateBranchComponent } from './Components/branch/create-branch/create-branch.component';
import { UpdateBranchComponent } from './Components/branch/update-branch/update-branch.component';
import { ListRoleComponent } from './Components/Role/role-list/role-list.component';
import { CreateRoleComponent } from './Components/Role/create-role/create-role.component';
import { EditRoleComponent } from './Components/Role/edit-role/edit-role.component';
import { CreateRateComponent } from './Components/Rate/create-rate/create-rate.component';
import { RateListComponent } from './Components/Rate/rate-list/rate-list.component';
import { UpdateRateComponent } from './Components/Rate/update-rate/update-rate.component';
import { CreateCollateralTypeComponent } from './Components/collateral_Type/create-collateral-type/create-collateral-type.component';
import { CollateralTypeListComponent } from './Components/collateral_Type/collateral-type-list/collateral-type-list.component';
import { CollateralTypeUpdateComponent } from './Components/collateral_Type/collateral-type-update/collateral-type-update.component';
import { CreateCollateralComponent } from './Components/Collateral/create-collateral/create-collateral.component';
import { CifListComponent } from './Components/CIF/cif-list/cif-list.component';
import { CreateCifComponent } from './Components/CIF/create-cif/create-cif.component';
import { CreateComponent } from './Components/CurrentACC/create/create.component';
import { ListComponent } from './Components/CurrentACC/list/list.component';
import { UpdateComponent } from './Components/CurrentACC/update/update.component';
import { CustomerDetailComponent } from './Components/Account/customer-detail/customer-detail.component';
import { DealerDetailComponent } from './Components/Account/dealer-detail/dealer-detail.component';
import { CifDetailComponent } from './Components/CIF/cif-detail/cif-detail.component';
import { AllproductListComponent } from './Components/Product/allproduct-list/allproduct-list.component';
import { HpLoanFormComponent } from './Components/hp-loan/hp-loan-form/hp-loan-form.component';
import { HpLoanListComponent } from './Components/hp-loan/hp-loan-list-approval/hp-loan-list.component';
import { HpLoanDetailComponent } from './Components/hp-loan/hp-loan-detail/hp-loan-detail.component';
import { HpLoanScheduleComponent } from './Components/hp-loan/hp-loan-schedule/hp-loan-schedule.component';
import { HpLoanListFinishedComponent } from './Components/hp-loan/hp-loan-list-finished/hp-loan-list-finished.component';
import { EntryDashboardComponent } from './Components/entry-dashboard/entry-dashboard.component';
import { ManagerDashboardComponent } from './Components/manager-dashboard/manager-dashboard.component';
import { OperationDashboardComponent } from './Components/operation-dashboard/operation-dashboard.component';
import { DealerDashboardComponent } from './Components/dealer-dashboard/dealer-dashboard.component';
import { CreateEditProductComponent } from './Components/Product/create-edit-product/create-edit-product.component';
import { SmeLoanDetailComponent } from './Components/sme-loan-detail/sme-loan-detail.component';
import { CreateDealerComponent } from './Components/Dealer/create-dealer/create-dealer.component';
import { DealerListComponent } from './Components/Dealer/dealer-list/dealer-list.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'admin-dashboard',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' },
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: BranchAccountCountComponent },
      { path: 'create-user', component: CreateuserComponent },
      { path: 'list-users', component: ListuserComponent },
      { path: 'edit-user/:id', component: UpdateUserComponent },
      { path: 'delete-user/:id', component: ListuserComponent },
      { path: 'branch-list', component: BranchListComponent },
      { path: 'create-branch', component: CreateBranchComponent },
      { path: 'update-branch/:id', component: UpdateBranchComponent },
      { path: 'list-role', component: ListRoleComponent },
      { path: 'create-role', component: CreateRoleComponent },
      { path: 'edit-role/:id', component: EditRoleComponent },
      { path: 'create-rate', component: CreateRateComponent },
      { path: 'list-rate', component: RateListComponent },
      { path: 'update-rate/:id', component: UpdateRateComponent },
      {path: 'create-collateral-type', component: CreateCollateralTypeComponent},
      {path: 'collateral-type-list', component: CollateralTypeListComponent},
      {path: 'update-collateral-type/:id', component: CollateralTypeUpdateComponent},
      
    ]
  },
  {
    path: 'entry-dashboard',
    component: EntryDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ENTRY' },
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: BranchAccountCountComponent },
      { path: 'current-account-register', component: CurrentAccountRegisterComponent },
      { path: 'current-acc-list2', component: CurrentAccListComponent },
      { path: 'transaction/create/:currentAccountId', component: TransactionComponent },
      { path: 'transaction', component: TransactionComponent },
      { path: 'transaction-history', component: TransactionHistoryComponent },
      { path: 'sme-loan-list', component: SmeLaonHistoryComponent },
      { path: 'cif-list', component: CifListComponent },
      { path: 'create-cif', component: CreateCifComponent },
      { path: 'updated-cif/:id', component: CreateCifComponent },
      { path: 'create-current-account/:cifId', component: CreateComponent },
      { path: 'current-acc-list', component: ListComponent },
      { path: 'update-current-account/:id', component: UpdateComponent },
      { path: 'cif-details/:id', component: CustomerDetailComponent },
      { path: 'create-dealer', component: CreateDealerComponent },
      { path: 'dealer-list', component: RateListComponent },
      { path: 'cif-detail-view/:id', component: CifDetailComponent },
      { path: 'dealer-detail', component: DealerDetailComponent },
      { path: 'create-collateral', component: CreateCollateralComponent }

    ]
  },
  {
    path: 'manager-dashboard',
    component: ManagerDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'MANAGER' },
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: BranchAccountCountComponent },
      { path: 'current-acc-list2', component: CurrentAccListComponent },
      { path: 'transaction-history', component: TransactionHistoryComponent },
      { path: 'dealer-list', component: DealerListComponent },
      { path: 'dealer-detail', component: DealerDetailComponent },
      { path: 'hp-loan-list', component: HpLoanListComponent },
      { path: 'sme-loan-list', component: SmeLaonHistoryComponent },

    ]
  },
  {
    path: 'operation-dashboard',
    component: OperationDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'OPERATION' },
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: BranchAccountCountComponent },
      { path: 'current-acc-list2', component: CurrentAccListComponent },
      { path: 'transaction-history', component: TransactionHistoryComponent },
      { path: 'hp-register', component: HpLoanFormComponent },
      { path: 'sme-loan-register', component: SmeLoanRegisterComponent },
      { path: 'hp-loan-detail/:id', component: HpLoanDetailComponent },
      { path: 'hp-loan-schedule/:id', component: HpLoanScheduleComponent },
      { path: 'hp-loan-list-finished', component: HpLoanListFinishedComponent },
      { path: 'all-list', component: AllproductListComponent },
      { path: 'sme-loan-detail/:id', component: SmeLoanDetailComponent},
      { path: 'sme-loan-list', component: SmeLaonHistoryComponent },
      



    ]
  },
  {
    path: 'dealer-dashboard',
    component: DealerDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'DEALER' },
    children: [
      { path: '', redirectTo: 'dealer-transaction', pathMatch: 'full' },
      { path: 'dealer-transaction', component: TransactionComponent },
      { path: 'create-product', component: CreateEditProductComponent },
      { path: 'procuct-list', component: AllproductListComponent },
      { path: 'all-list', component: AllproductListComponent }
    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }