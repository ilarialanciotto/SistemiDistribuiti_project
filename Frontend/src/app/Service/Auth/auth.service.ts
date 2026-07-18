import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {UserDTO} from '../../Model/user.model';
import {JwtResponse} from '../../Model/response_request.model';
import {LoggedInUser} from '../../Model/loggedUser.model';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject: BehaviorSubject<LoggedInUser | null>;
  currentUser$: Observable<LoggedInUser | null>;

  constructor(private http: HttpClient) {
    const storedUserString = localStorage.getItem('loggedInUser');
    const initialUser: LoggedInUser | null = storedUserString ? JSON.parse(storedUserString) : null;
    this.currentUserSubject = new BehaviorSubject<LoggedInUser | null>(initialUser);
    this.currentUser$ = this.currentUserSubject.asObservable();
  }


  register(user: UserDTO): Observable<string> {
    const url = `${this.apiUrl}/register`;
    return this.http.post(url, user, { responseType: 'text' });
  }

  login(user: UserDTO): Observable<JwtResponse> {
    const url = `${this.apiUrl}/login`;
    return this.http.post<JwtResponse>(url, user).pipe(
      tap(res => {
        if (res && res.jwt && res.username && res.role) {
          localStorage.setItem('token', res.jwt);
          const userData: LoggedInUser = { username: res.username, role: res.role};
          localStorage.setItem('loggedInUser', JSON.stringify(userData));
          this.currentUserSubject.next(userData);
        } else {
          this.logout();
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('loggedInUser');
    this.currentUserSubject.next(null);
  }

  getHeader(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }


}



