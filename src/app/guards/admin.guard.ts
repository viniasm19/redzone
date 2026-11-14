import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Guard: deixa entrar só o administrador.
export const adminGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAdmin()) {
    return true;
  }

  if (auth.isAuthenticated()) {
    return router.createUrlTree(['/']);
  }

  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};