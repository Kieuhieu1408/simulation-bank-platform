import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/auth.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <!-- Sidebar -->
      <nav class="sidebar">
        <div class="sidebar-brand">
          <span class="brand-icon">🏦</span>
          <span class="brand-name">CMS Banking</span>
        </div>

        <ul class="nav-list">
          @for (item of navItems; track item.route) {
            <li>
              <a
                [routerLink]="item.route"
                routerLinkActive="active"
                class="nav-link"
              >
                <span class="nav-icon">{{ item.icon }}</span>
                <span class="nav-label">{{ item.label }}</span>
              </a>
            </li>
          }
        </ul>

        <div class="sidebar-footer">
          <button class="btn-logout" (click)="logout()">
            <span>⬡</span> Đăng xuất
          </button>
        </div>
      </nav>

      <!-- Main content area -->
      <div class="content-area">
        <header class="topbar">
          <span class="topbar-title">Hệ thống quản trị ngân hàng</span>
        </header>
        <main class="main-content">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: [`
    .shell {
      display: flex;
      min-height: 100vh;
      font-family: 'Inter', 'Segoe UI', sans-serif;
    }

    /* ---- Sidebar ---- */
    .sidebar {
      width: 240px;
      flex-shrink: 0;
      background: #1e293b;
      color: #f8fafc;
      display: flex;
      flex-direction: column;
    }

    .sidebar-brand {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 20px 16px;
      border-bottom: 1px solid #334155;
      font-size: 1.1rem;
      font-weight: 700;
      letter-spacing: 0.01em;
    }

    .brand-icon { font-size: 1.4rem; }

    .nav-list {
      list-style: none;
      margin: 0;
      padding: 12px 0;
      flex: 1;
    }

    .nav-link {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 20px;
      color: #cbd5e1;
      text-decoration: none;
      border-radius: 6px;
      margin: 2px 8px;
      transition: background 0.15s, color 0.15s;
      font-size: 0.9rem;
    }

    .nav-link:hover {
      background: #334155;
      color: #f8fafc;
    }

    .nav-link.active {
      background: #2563eb;
      color: #fff;
      font-weight: 600;
    }

    .nav-icon { font-size: 1rem; width: 20px; text-align: center; }

    .sidebar-footer {
      padding: 16px;
      border-top: 1px solid #334155;
    }

    .btn-logout {
      display: flex;
      align-items: center;
      gap: 8px;
      width: 100%;
      padding: 8px 12px;
      background: transparent;
      color: #94a3b8;
      border: 1px solid #334155;
      border-radius: 6px;
      cursor: pointer;
      font-size: 0.875rem;
      transition: background 0.15s, color 0.15s;
    }

    .btn-logout:hover {
      background: #7f1d1d;
      color: #fff;
      border-color: #7f1d1d;
    }

    /* ---- Content area ---- */
    .content-area {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #f1f5f9;
      min-width: 0;
    }

    .topbar {
      background: #fff;
      border-bottom: 1px solid #e2e8f0;
      padding: 14px 24px;
      font-size: 0.875rem;
      color: #64748b;
    }

    .topbar-title { font-weight: 600; color: #1e293b; }

    .main-content {
      flex: 1;
      padding: 0;
      overflow-y: auto;
    }
  `],
})
export class ShellComponent {
  private readonly auth = inject(AuthService);

  readonly navItems: NavItem[] = [
    { label: 'Tra cứu khách hàng', icon: '🔍', route: '/customers' },
    { label: 'Quản lý Proposal',   icon: '📋', route: '/proposals' },
    { label: 'Tài liệu',           icon: '📄', route: '/documents' },
    { label: 'Quản trị',           icon: '⚙️',  route: '/administration' },
  ];

  logout(): void {
    this.auth.logout();
  }
}
