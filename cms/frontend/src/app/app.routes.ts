import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell.component';
import { CustomerLookupComponent } from './features/customer-lookup/customer-lookup.component';
import { ProposalsComponent } from './features/proposals/proposals.component';
import { DocumentsComponent } from './features/documents/documents.component';
import { AdministrationComponent } from './features/administration/administration.component';
import { AuthCallbackComponent } from './core/auth-callback.component';
import { authGuard } from './core/auth.guard';
import { permissionGuard } from './core/permission.guard';

export const routes: Routes = [
  // OAuth2 PKCE callback — must be public, no guard
  {
    path: 'auth/callback',
    component: AuthCallbackComponent,
  },

  // Bare root redirect
  {
    path: '',
    redirectTo: 'customers',
    pathMatch: 'full',
  },

  // Shell layout wraps all authenticated routes
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'customers',
        component: CustomerLookupComponent,
        canActivate: [permissionGuard],
        data: { menuCode: 'user_info', action: 'VIEW' },
        title: 'Tra cứu khách hàng',
      },
      {
        path: 'proposals',
        component: ProposalsComponent,
        canActivate: [permissionGuard],
        data: { menuCode: 'proposal_management', action: 'VIEW' },
        title: 'Quản lý Proposal',
      },
      {
        path: 'documents',
        component: DocumentsComponent,
        canActivate: [permissionGuard],
        data: { menuCode: 'documents', action: 'VIEW' },
        title: 'Tài liệu',
      },
      {
        path: 'administration',
        component: AdministrationComponent,
        canActivate: [permissionGuard],
        data: { menuCode: 'administration', action: 'VIEW' },
        title: 'Quản trị hệ thống',
      },
    ],
  },

  // Catch-all
  {
    path: '**',
    redirectTo: 'customers',
  },
];

