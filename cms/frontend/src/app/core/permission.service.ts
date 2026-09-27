import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface UserPermissions {
  /** key: menuCode, value: list of allowed action strings (e.g. "VIEW", "CREATE") */
  [menuCode: string]: string[];
}

@Injectable({ providedIn: 'root' })
export class PermissionService {
  private readonly http = inject(HttpClient);

  private readonly _permissions$ = new BehaviorSubject<UserPermissions>({});
  readonly permissions$ = this._permissions$.asObservable();

  /** Load permissions from the backend. Call once after successful login. */
  loadPermissions(): Observable<UserPermissions> {
    return this.http.get<UserPermissions>('/api/v1/me/permissions').pipe(
      tap((perms) => this._permissions$.next(perms))
    );
  }

  /**
   * Check whether the current user has the given action on the given menu code.
   * @param menuCode  e.g. "user_info" | "proposal_management"
   * @param action    e.g. "VIEW" | "CREATE" | "APPROVE"
   */
  hasPermission(menuCode: string, action: string): boolean {
    const perms = this._permissions$.getValue();
    return perms[menuCode]?.includes(action) ?? false;
  }

  /** Clear cached permissions on logout. */
  clearPermissions(): void {
    this._permissions$.next({});
  }
}
