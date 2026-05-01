import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { DashboardResponse } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  dashboard: DashboardResponse | null = null;
  loading = true;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.http.get<DashboardResponse>('/api/dashboard')
      .subscribe({
        next: data => {
          this.dashboard = data;
          this.loading = false;
        },
        error: () => this.loading = false
      });
  }

  logout(): void {
    this.authService.logout();
  }
}
