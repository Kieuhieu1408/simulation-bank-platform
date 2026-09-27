import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CustomerResponse } from '../../shared/models/customer.model';

@Component({
  selector: 'app-customer-lookup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-wrapper">
      <h2 class="page-title">Tra cứu khách hàng</h2>

      <!-- Search bar -->
      <div class="search-bar">
        <input
          type="text"
          [formControl]="cccdControl"
          placeholder="Nhập số CCCD để tìm kiếm..."
          class="search-input"
        />
        <button
          class="btn btn-primary"
          [disabled]="cccdControl.invalid || loading()"
          (click)="search()"
        >
          {{ loading() ? 'Đang tìm...' : 'Tìm kiếm' }}
        </button>
      </div>

      <!-- Validation hint -->
      @if (cccdControl.touched && cccdControl.hasError('required')) {
        <p class="error-hint">Vui lòng nhập số CCCD.</p>
      }
      @if (cccdControl.touched && cccdControl.hasError('minlength')) {
        <p class="error-hint">CCCD phải có ít nhất 9 ký tự.</p>
      }

      <!-- Error message -->
      @if (errorMsg()) {
        <div class="alert alert-error">{{ errorMsg() }}</div>
      }

      <!-- Results table -->
      @if (customers().length > 0) {
        <table class="data-table">
          <thead>
            <tr>
              <th>CIF</th>
              <th>Họ tên</th>
              <th>Số điện thoại</th>
              <th>CCCD</th>
            </tr>
          </thead>
          <tbody>
            @for (c of customers(); track c.cif) {
              <tr>
                <td>{{ c.cif }}</td>
                <td>{{ c.fullName }}</td>
                <td>{{ c.phone }}</td>
                <td>{{ c.cccd }}</td>
              </tr>
            }
          </tbody>
        </table>
      }

      @if (!loading() && searched() && customers().length === 0 && !errorMsg()) {
        <p class="no-result">Không tìm thấy khách hàng phù hợp.</p>
      }
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 24px; }
    .page-title { margin-bottom: 16px; font-size: 1.25rem; font-weight: 600; }
    .search-bar { display: flex; gap: 8px; margin-bottom: 8px; }
    .search-input { flex: 1; padding: 8px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.95rem; }
    .btn { padding: 8px 20px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.95rem; }
    .btn-primary { background: #2563eb; color: #fff; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .error-hint { color: #dc2626; font-size: 0.85rem; margin: 4px 0; }
    .alert { padding: 10px 14px; border-radius: 6px; margin: 12px 0; }
    .alert-error { background: #fee2e2; color: #991b1b; }
    .data-table { width: 100%; border-collapse: collapse; margin-top: 16px; }
    .data-table th, .data-table td { border: 1px solid #e5e7eb; padding: 10px 14px; text-align: left; }
    .data-table th { background: #f9fafb; font-weight: 600; }
    .data-table tr:hover td { background: #f0f9ff; }
    .no-result { color: #6b7280; margin-top: 16px; }
  `],
})
export class CustomerLookupComponent {
  private readonly http = inject(HttpClient);

  readonly cccdControl = new FormControl('', [
    Validators.required,
    Validators.minLength(9),
  ]);

  readonly customers = signal<CustomerResponse[]>([]);
  readonly loading = signal(false);
  readonly searched = signal(false);
  readonly errorMsg = signal<string | null>(null);

  search(): void {
    if (this.cccdControl.invalid) {
      this.cccdControl.markAsTouched();
      return;
    }

    const cccd = this.cccdControl.value!.trim();
    const params = new HttpParams().set('cccd', cccd);

    this.loading.set(true);
    this.errorMsg.set(null);
    this.customers.set([]);

    this.http
      .get<CustomerResponse>('/api/v1/customers/search', { params })
      .subscribe({
        next: (data) => {
          // Backend returns a single CustomerResponse, wrap in array for the table
          this.customers.set(data ? [data] : []);
          this.searched.set(true);
          this.loading.set(false);
        },
        error: (err) => {
          this.errorMsg.set(
            err?.error?.message ?? 'Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại.'
          );
          this.searched.set(true);
          this.loading.set(false);
        },
      });
  }
}
