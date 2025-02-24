import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateBranchComponent } from './Components/branch/create-branch/create-branch.component';
import { ListBranchComponent } from './Components/branch/list-branch/list-branch.component';
import { UpdateBranchComponent } from './Components/branch/update-branch/update-branch.component';
import { CifRegisterComponent } from './Components/cif-register/cif-register.component';
import { TransactionComponent } from './Components/transaction/transaction.component';
import { TransactionHistoryComponent } from './Components/transaction-history/transaction-history.component';
import { CurrentAccListComponent } from './Components/current-acc-list/current-acc-list.component';
import { CurrentAccountRegisterComponent } from './Components/current-account-register/current-account-register.component';
import { HomeComponent } from './Components/home/home.component';
import { SmeLaonRegisterComponent } from './Components/sme-laon-register/sme-laon-register.component';
import { CollateralComponent } from './Components/collateral/collateral.component';
import { SmeLoanListComponent } from './Components/sme-loan-list/sme-loan-list.component';
import { CollateralTypeComponent } from './Components/collateral-type/collateral-type.component';
import { LoginComponent } from './Components/login/login.component';
import { DashboardComponent } from './Components/dashboard/dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { ListuserComponent } from './Components/user/listuser/listuser.component';
import { CreateuserComponent } from './Components/user/createuser/createuser.component';


const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent, canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'cif-register', component: CifRegisterComponent },
      { path: 'current-account-register', component: CurrentAccountRegisterComponent },
      { path: 'current-acc-list', component: CurrentAccListComponent },
      { path: 'transaction', component: TransactionComponent },
      { path: 'transaction-history', component: TransactionHistoryComponent },
      { path: 'sme-loan-register', component: SmeLaonRegisterComponent },
      { path: 'sme-loan-list', component: SmeLoanListComponent },
      { path: 'collateral', component: CollateralComponent },
      { path: 'collateral-type', component: CollateralTypeComponent },
      { path: 'create-user', component: CreateuserComponent },
      { path: 'list-users', component: ListuserComponent },
      { path: 'edit-user/:id', component: CreateuserComponent },
      { path: 'delete-user/:id', component: ListuserComponent },
      { path: 'create-branch', component: CreateBranchComponent },
      { path: 'list-branch', component: ListBranchComponent },
      { path: 'update-branch/:id', component: UpdateBranchComponent },

    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
