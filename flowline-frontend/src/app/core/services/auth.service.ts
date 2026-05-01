import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = '/api/auth';
  private readonly KEY  = 'flowline_token';
  private readonly ROLE = 'flowline_role';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem(this.KEY,  response.token);
        localStorage.setItem(this.ROLE, response.role);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.KEY);
    localStorage.removeItem(this.ROLE);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.KEY);
  }

  getRole(): string | null {
    return localStorage.getItem(this.ROLE);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
}
