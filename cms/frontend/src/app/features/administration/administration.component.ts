import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-administration',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-wrapper">
      <h2 class="page-title">Quản trị hệ thống</h2>
      <p class="placeholder">Chức năng quản trị đang được phát triển.</p>
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 24px; }
    .page-title { font-size: 1.25rem; font-weight: 600; margin-bottom: 12px; }
    .placeholder { color: #6b7280; }
  `],
})
export class AdministrationComponent {}
