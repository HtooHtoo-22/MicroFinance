import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CifRegisterComponent } from './Components/cif-register/cif-register.component';
import { TransactionComponent } from './Components/transaction/transaction.component';
import { TransactionHistoryComponent } from './Components/transaction-history/transaction-history.component';
import { CurrentAccListComponent } from './Components/current-acc-list/current-acc-list.component';
import { LoginComponent } from './Components/login/login.component';
import { DashboardComponent } from './Components/dashboard/dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { ListuserComponent } from './Components/user/listuser/listuser.component';
import { CreateuserComponent } from './Components/user/createuser/createuser.component';
import { SmeLaonHistoryComponent } from './Components/sme-laon-history/sme-laon-history.component';
import { CurrentAccountRegisterComponent } from './Components/current-acc-register/current-acc-register.component';
import { SmeLoanRegisterComponent } from './Components/sme-loan-register/sme-loan-register.component';
import { BranchListComponent } from './Components/branch/branch-list/branch-list.component';
import { CreateBranchComponent } from './Components/branch/create-branch/create-branch.component';
import { UpdateBranchComponent } from './Components/branch/update-branch/update-branch.component';
import { UpdateUserComponent } from './Components/user/update-user/update-user.component';
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
import { CreateComponent,  } from './Components/CurrentACC/create/create.component';
import { UpdateComponent } from './Components/CurrentACC/update/update.component';
import { ListComponent } from './Components/CurrentACC/list/list.component';
import { CustomerDetailComponent } from './Components/Account/customer-detail/customer-detail.component';
import { CreateDealerComponent } from './Components/Dealer/create-dealer/create-dealer.component';
import { DealerListComponent } from './Components/Dealer/dealer-list/dealer-list.component';
import { BranchAccountCountComponent } from './Components/counting/branch-account-count/branch-account-count.component';
import { BranchUserCountComponent } from './Components/counting/branch-user-count/branch-user-count.component';
import { ListCollateralComponent } from './Components/Collateral/list-collateral/list-collateral.component';
import { CollateralDetailComponent } from './Components/Collateral/collateral-detail/collateral-detail.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent, canActivate: [AuthGuard],
    children: [
      { path: 'home', component: BranchAccountCountComponent },
      { path: 'cif-register', component: CifRegisterComponent },
      { path: 'current-account-register', component: CurrentAccountRegisterComponent },
      { path: 'current-acc-list2', component: CurrentAccListComponent },
      { path: 'transaction/create/:currentAccountId', component: TransactionComponent },
      { path: 'collateralDetail/:id',component:CollateralDetailComponent},
      { path: 'transaction', component: TransactionComponent },
      { path: 'transaction-history', component: TransactionHistoryComponent },
      { path: 'sme-loan-register', component: SmeLoanRegisterComponent },
      { path: 'sme-loan-list', component: SmeLaonHistoryComponent },
      { path: 'create-user', component: CreateuserComponent },
      { path: 'list-users', component: ListuserComponent },
      { path: 'edit-user/:id', component: UpdateUserComponent }, // Add this route
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
      { path: 'create-collateral-type', component: CreateCollateralTypeComponent },
      { path: 'collateral-type-list', component: CollateralTypeListComponent },
      { path: 'edit-collateral-type/:id', component: CollateralTypeUpdateComponent },
      { path: 'create-collateral', component: CreateCollateralComponent },
      { path: 'list-collateral', component: ListCollateralComponent },
      { path: 'cif-list', component: CifListComponent },
      { path: 'create-cif', component: CreateCifComponent },
      { path: 'updated-cif/:id', component: CreateCifComponent },
      { path: 'create-current-account/:cifId', component: CreateComponent },
      { path: 'current-acc-list', component: ListComponent },
      { path: 'update-current-account/:id', component: UpdateComponent },
      { path: 'cif-details/:id', component: CustomerDetailComponent },
      { path: 'create-dealer', component: CreateDealerComponent },
      { path: 'dealer-list', component: DealerListComponent }
    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
