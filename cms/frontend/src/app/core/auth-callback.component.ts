import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `<p style="padding:24px">Đang xử lý đăng nhập...</p>`,
})
export class AuthCallbackComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  async ngOnInit(): Promise<void> {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const state = params.get('state');

    if (!code || !state) {
      await this.router.navigate(['/']);
      return;
    }

    try {
      await this.auth.handleCallback(code, state);
      await this.router.navigate(['/customers']);
    } catch (err) {
      console.error('Auth callback failed:', err);
      await this.router.navigate(['/']);
    }
  }
}
