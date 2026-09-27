import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Proposal } from '../../shared/models/proposal.model';

/** Mock seed data shown before any real API call */
const MOCK_PROPOSALS: Proposal[] = [
  {
    id: '1',
    proposalCode: 'PR-2024-0001',
    customerCif: 'CIF001234',
    proposalType: 'CHANGE_PHONE',
    status: 'PENDING',
    makerId: 'EMP001',
    checkerId: null,
    createdAt: '2024-06-01T08:00:00Z',
  },
  {
    id: '2',
    proposalCode: 'PR-2024-0002',
    customerCif: 'CIF005678',
    proposalType: 'CHANGE_CCCD',
    status: 'APPROVED',
    makerId: 'EMP002',
    checkerId: 'EMP010',
    createdAt: '2024-06-02T09:30:00Z',
  },
  {
    id: '3',
    proposalCode: 'PR-2024-0003',
    customerCif: 'CIF009999',
    proposalType: 'CHANGE_PWD',
    status: 'REJECTED',
    makerId: 'EMP003',
    checkerId: 'EMP010',
    createdAt: '2024-06-03T11:15:00Z',
  },
];

@Component({
  selector: 'app-proposals',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <h2 class="page-title">Quản lý Proposal</h2>
        <button class="btn btn-primary" (click)="createProposal()">
          + Tạo yêu cầu mới
        </button>
      </div>

      @if (errorMsg()) {
        <div class="alert alert-error">{{ errorMsg() }}</div>
      }

      @if (successMsg()) {
        <div class="alert alert-success">{{ successMsg() }}</div>
      }

      <table class="data-table">
        <thead>
          <tr>
            <th>Mã yêu cầu</th>
            <th>CIF khách hàng</th>
            <th>Loại yêu cầu</th>
            <th>Trạng thái</th>
            <th>Maker</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          @for (p of proposals(); track p.id) {
            <tr>
              <td>{{ p.proposalCode }}</td>
              <td>{{ p.customerCif }}</td>
              <td>
                <span class="badge badge-type">{{ typeLabel(p.proposalType) }}</span>
              </td>
              <td>
                <span class="badge" [class]="statusClass(p.status)">
                  {{ statusLabel(p.status) }}
                </span>
              </td>
              <td>{{ p.makerId }}</td>
              <td>{{ p.createdAt | date: 'dd/MM/yyyy HH:mm' }}</td>
              <td>
                @if (p.status === 'PENDING') {
                  <button
                    class="btn btn-approve"
                    [disabled]="loadingId() === p.id"
                    (click)="approveProposal(p.id)"
                  >Duyệt</button>
                  <button
                    class="btn btn-reject"
                    [disabled]="loadingId() === p.id"
                    (click)="rejectProposal(p.id)"
                  >Từ chối</button>
                } @else {
                  <span class="no-action">—</span>
                }
              </td>
            </tr>
          }
        </tbody>
      </table>

      @if (proposals().length === 0) {
        <p class="no-result">Chưa có yêu cầu nào.</p>
      }
    </div>
  `,
  styles: [`
    .page-wrapper { padding: 24px; }
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .page-title { font-size: 1.25rem; font-weight: 600; }
    .btn { padding: 7px 16px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.875rem; margin-right: 4px; }
    .btn-primary { background: #2563eb; color: #fff; }
    .btn-approve { background: #16a34a; color: #fff; }
    .btn-reject { background: #dc2626; color: #fff; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .alert { padding: 10px 14px; border-radius: 6px; margin-bottom: 12px; }
    .alert-error { background: #fee2e2; color: #991b1b; }
    .alert-success { background: #dcfce7; color: #166534; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { border: 1px solid #e5e7eb; padding: 10px 14px; text-align: left; font-size: 0.875rem; }
    .data-table th { background: #f9fafb; font-weight: 600; }
    .data-table tr:hover td { background: #f0f9ff; }
    .badge { display: inline-block; padding: 2px 8px; border-radius: 12px; font-size: 0.78rem; font-weight: 600; }
    .badge-type { background: #e0e7ff; color: #3730a3; }
    .badge-pending { background: #fef9c3; color: #854d0e; }
    .badge-approved { background: #dcfce7; color: #166534; }
    .badge-rejected { background: #fee2e2; color: #991b1b; }
    .badge-cancelled { background: #f3f4f6; color: #6b7280; }
    .no-action { color: #9ca3af; }
    .no-result { color: #6b7280; margin-top: 16px; }
  `],
})
export class ProposalsComponent implements OnInit {
  private readonly http = inject(HttpClient);

  readonly proposals = signal<Proposal[]>([]);
  readonly loadingId = signal<string | null>(null);
  readonly errorMsg = signal<string | null>(null);
  readonly successMsg = signal<string | null>(null);

  ngOnInit(): void {
    // Start with mock data; replace with real API call if available
    this.proposals.set([...MOCK_PROPOSALS]);
  }

  createProposal(): void {
    // POST /api/v1/proposals — body would come from a modal/form in a real implementation
    const body = {
      customerCif: 'CIF_NEW',
      proposalType: 'CHANGE_PHONE',
    };

    this.http.post<Proposal>('/api/v1/proposals', body).subscribe({
      next: (created) => {
        this.proposals.update((list) => [created, ...list]);
        this.showSuccess('Tạo yêu cầu thành công.');
      },
      error: () => this.showError('Không thể tạo yêu cầu. Vui lòng thử lại.'),
    });
  }

  approveProposal(id: string): void {
    this.loadingId.set(id);
    this.http.post<void>(`/api/v1/proposals/${id}/approve`, {}).subscribe({
      next: () => this.removeFromList(id, 'APPROVED'),
      error: () => this.showError('Duyệt yêu cầu thất bại.'),
      complete: () => this.loadingId.set(null),
    });
  }

  rejectProposal(id: string): void {
    this.loadingId.set(id);
    this.http.post<void>(`/api/v1/proposals/${id}/reject`, {}).subscribe({
      next: () => this.removeFromList(id, 'REJECTED'),
      error: () => this.showError('Từ chối yêu cầu thất bại.'),
      complete: () => this.loadingId.set(null),
    });
  }

  // ---------------------------------------------------------------- helpers --

  typeLabel(type: Proposal['proposalType']): string {
    const map: Record<Proposal['proposalType'], string> = {
      CHANGE_PWD: 'Đổi mật khẩu',
      CHANGE_PHONE: 'Đổi SĐT',
      CHANGE_CCCD: 'Đổi CCCD',
      CHANGE_CIF: 'Đổi CIF',
    };
    return map[type] ?? type;
  }

  statusLabel(status: Proposal['status']): string {
    const map: Record<Proposal['status'], string> = {
      PENDING: 'Chờ duyệt',
      APPROVED: 'Đã duyệt',
      REJECTED: 'Từ chối',
      CANCELLED: 'Đã hủy',
    };
    return map[status] ?? status;
  }

  statusClass(status: Proposal['status']): string {
    return `badge-${status.toLowerCase()}`;
  }

  private removeFromList(id: string, finalStatus: Proposal['status']): void {
    // Backend moves approved/rejected proposals to change_log and deletes from proposal table.
    // Update the local UI to reflect the new status without relying on the void response body.
    this.proposals.update((list) =>
      list.map((p) => (p.id === id ? { ...p, status: finalStatus } : p))
    );
    this.showSuccess('Cập nhật trạng thái thành công.');
  }

  private replaceInList(updated: Proposal): void {
    this.proposals.update((list) =>
      list.map((p) => (p.id === updated.id ? updated : p))
    );
    this.showSuccess('Cập nhật trạng thái thành công.');
  }

  private showError(msg: string): void {
    this.errorMsg.set(msg);
    this.successMsg.set(null);
    setTimeout(() => this.errorMsg.set(null), 5000);
  }

  private showSuccess(msg: string): void {
    this.successMsg.set(msg);
    this.errorMsg.set(null);
    setTimeout(() => this.successMsg.set(null), 4000);
  }
}
