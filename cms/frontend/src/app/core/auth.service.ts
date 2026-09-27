import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

interface TokenSet {
  accessToken: string;
  idToken: string;
  refreshToken: string;
  expiresAt: number; // epoch ms
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly KEYCLOAK_BASE = 'http://localhost:8080/realms/simulation-bank';
  private readonly CLIENT_ID = 'cms-frontend';
  private readonly REDIRECT_URI = `${window.location.origin}/auth/callback`;

  /** Token stored in-memory only — never written to localStorage */
  private tokenSet: TokenSet | null = null;

  private readonly _isAuthenticated$ = new BehaviorSubject<boolean>(false);
  readonly isAuthenticated$ = this._isAuthenticated$.asObservable();

  // ------------------------------------------------------------------ PKCE --

  private generateCodeVerifier(): string {
    const array = new Uint8Array(64);
    crypto.getRandomValues(array);
    return btoa(String.fromCharCode(...array))
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=/g, '');
  }

  private async generateCodeChallenge(verifier: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return btoa(String.fromCharCode(...new Uint8Array(digest)))
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=/g, '');
  }

  // ----------------------------------------------------------------- login --

  async login(): Promise<void> {
    const verifier = this.generateCodeVerifier();
    const challenge = await this.generateCodeChallenge(verifier);
    const state = crypto.randomUUID();

    // Persist both verifier and state for CSRF validation in handleCallback
    sessionStorage.setItem('pkce_verifier', verifier);
    sessionStorage.setItem('pkce_state', state);

    const params = new URLSearchParams({
      response_type: 'code',
      client_id: this.CLIENT_ID,
      redirect_uri: this.REDIRECT_URI,
      scope: 'openid profile email',
      code_challenge: challenge,
      code_challenge_method: 'S256',
      state,
    });

    window.location.href = `${this.KEYCLOAK_BASE}/protocol/openid-connect/auth?${params}`;
  }

  // ------------------------------------------------- handle OAuth2 callback --

  async handleCallback(code: string, incomingState: string): Promise<void> {
    // Verify CSRF state before exchanging the code
    const savedState = sessionStorage.getItem('pkce_state');
    sessionStorage.removeItem('pkce_state');
    if (!savedState || savedState !== incomingState) {
      throw new Error('OAuth2 state mismatch — possible CSRF attack');
    }

    const verifier = sessionStorage.getItem('pkce_verifier');
    if (!verifier) {
      throw new Error('Missing PKCE verifier — callback called without a prior login()');
    }
    sessionStorage.removeItem('pkce_verifier');

    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: this.CLIENT_ID,
      redirect_uri: this.REDIRECT_URI,
      code,
      code_verifier: verifier,
    });

    const response = await fetch(
      `${this.KEYCLOAK_BASE}/protocol/openid-connect/token`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString(),
      }
    );

    if (!response.ok) {
      const err = await response.text();
      throw new Error(`Token exchange failed: ${err}`);
    }

    const json = await response.json();
    this.storeTokens(json);
  }

  // ---------------------------------------------------------------- logout --

  logout(): void {
    // Use the ID token (not access token) as id_token_hint to properly close Keycloak SSO session
    const idToken = this.tokenSet?.idToken ?? '';
    this.tokenSet = null;
    this._isAuthenticated$.next(false);

    const params = new URLSearchParams({
      client_id: this.CLIENT_ID,
      post_logout_redirect_uri: window.location.origin,
      id_token_hint: idToken,
    });

    window.location.href =
      `${this.KEYCLOAK_BASE}/protocol/openid-connect/logout?${params}`;
  }

  // ------------------------------------------------------- token accessors --

  getAccessToken(): string | null {
    if (!this.tokenSet) return null;
    if (Date.now() >= this.tokenSet.expiresAt) {
      // Token expired — trigger silent refresh or logout
      this.tokenSet = null;
      this._isAuthenticated$.next(false);
      return null;
    }
    return this.tokenSet.accessToken;
  }

  isAuthenticated(): boolean {
    return this.getAccessToken() !== null;
  }

  // -------------------------------------------------------- store in-memory --

  private storeTokens(json: {
    access_token: string;
    id_token: string;
    refresh_token: string;
    expires_in: number;
  }): void {
    this.tokenSet = {
      accessToken: json.access_token,
      idToken: json.id_token,
      refreshToken: json.refresh_token,
      expiresAt: Date.now() + json.expires_in * 1000,
    };
    this._isAuthenticated$.next(true);
  }
}
