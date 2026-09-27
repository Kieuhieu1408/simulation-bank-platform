import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-wrapper">
      <h2 class="page-title">Tài liệu</h2>
      <p class="placeholder">Chức năng quản lý tài liệu đang được phát triển.</p>
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 24px; }
    .page-title { font-size: 1.25rem; font-weight: 600; margin-bottom: 12px; }
    .placeholder { color: #6b7280; }
  `],
})
export class DocumentsComponent {}
