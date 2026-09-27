import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  // Attach the in-memory access token (never reads from localStorage)
  const token = auth.getAccessToken();

  let newReq = req.clone({
    setHeaders: {
      'X-Correlation-ID': crypto.randomUUID(),
    },
  });

  if (token) {
    newReq = newReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(newReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Token expired or revoked — redirect to Keycloak
        auth.login();
      } else if (error.status === 403) {
        console.error('Forbidden — insufficient permissions for this resource.');
      }
      return throwError(() => error);
    })
  );
};
