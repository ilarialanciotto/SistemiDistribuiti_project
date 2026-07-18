import {Component, inject, OnInit} from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../Service/Auth/auth.service';
import { Router } from '@angular/router';
import { UserDTO } from '../../Model/user.model';


  @Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    standalone: false,
    styleUrl: './register.component.css'
  })

  export class RegisterComponent implements OnInit{

    registerData: UserDTO = { name: '', email: '', password: '' };
    success = '';
    error = '';

    constructor(private authService: AuthService,private toastr: ToastrService,private router: Router) {}

    onRegister() {
    this.authService.register(this.registerData).subscribe({
      next: res => {
        this.toastr.info(res,"Register");
        this.error = '';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1000);
      },
      error: err => {
        this.toastr.error(err.error,"Register");
        this.success = '';
      }
    });
  }

  ngOnInit(): void {
  }
}
