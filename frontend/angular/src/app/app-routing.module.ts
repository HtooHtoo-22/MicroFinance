import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateBranchComponent } from './Components/branch/create-branch/create-branch.component';
import { ListBranchComponent } from './Components/branch/list-branch/list-branch.component';
import { UpdateBranchComponent } from './Components/branch/update-branch/update-branch.component';

const routes: Routes = [
  { path: 'create-branch', component: CreateBranchComponent },
  { path: 'list-branch', component: ListBranchComponent },
  { path: 'update-branch/:id', component: UpdateBranchComponent },

  { path: '', redirectTo: '/list-branch', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
