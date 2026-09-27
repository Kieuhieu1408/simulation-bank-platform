import { inject } from '@angular/core';
import { CanActivateFn, ActivatedRouteSnapshot } from '@angular/router';
import { PermissionService } from './permission.service';

/**
 * Route permission guard.
 *
 * Usage in route definition:
 * ```ts
 * {
 *   path: 'customers',
 *   canActivate: [authGuard, permissionGuard],
 *   data: { menuCode: 'user_info', action: 'VIEW' },
 *   component: CustomerLookupComponent,
 * }
 * ```
 */
export const permissionGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const permService = inject(PermissionService);

  const menuCode: string = route.data?.['menuCode'];
  const action: string = route.data?.['action'];

  if (!menuCode || !action) {
    console.warn('permissionGuard: route is missing data.menuCode or data.action');
    return false;
  }

  const allowed = permService.hasPermission(menuCode, action);

  if (!allowed) {
    console.warn(`permissionGuard: access denied — menuCode="${menuCode}", action="${action}"`);
  }

  return allowed;
};
