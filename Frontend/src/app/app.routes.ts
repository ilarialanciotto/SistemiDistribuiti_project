import {RouterModule, Routes} from '@angular/router';
import { LoginComponent } from './Dashboard/Login/login.component';
import { RegisterComponent } from './Dashboard/Register/register.component';
import {DashboardComponent} from './Dashboard/User/dashboard.component';
import {AuthGuard} from './Service/Auth/auth.guard';
import {NgModule} from '@angular/core';
import {DashboardAdminComponent} from './Dashboard/Admin/dashboardAdmin.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  { path: 'dashboardAdmin',
    component: DashboardAdminComponent,
    canActivate: [AuthGuard]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
