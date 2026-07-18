import {AuthService} from '../../Service/Auth/auth.service';
import {Component} from '@angular/core';
import {JwtResponse} from '../../Model/response_request.model';
import {UserDTO} from '../../Model/user.model';
import { ToastrService } from 'ngx-toastr';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: false,
  styleUrl: './login.component.css'
})

export class LoginComponent {

  loginData: UserDTO = { email: '', password: '' };
  jwtResponse: JwtResponse | null = null;
  hidePassword = true;

  constructor(private authService: AuthService, private router: Router, private toastr: ToastrService) {}

  onLogin() {
    this.authService.login(this.loginData).subscribe({
      next: res => {
        this.jwtResponse = res;
        if(res.role === 'ROLE_user')  this.router.navigate(['/dashboard']);
        else this.router.navigate(['/dashboardAdmin']);
      },
      error: (err ) => {
        this.toastr.error(err.error);
        this.jwtResponse = null;
      }
    });
  }
}
