import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Libera a rota só para o administrador.
// logado mas não admin -> volta para a Home
export const adminGuard: CanActivateFn = (_route, state) => {
  // 1) pega o AuthService e o Router
  const auth = inject(AuthService);
  const router = inject(Router);

  // 2) é o administrador: libera
  if (auth.isAdmin()) {
    return true;
  }

  // 3) está logado, mas é cliente: volta para a Home
  if (auth.isAuthenticated()) {
    return router.createUrlTree(['/']);
  }

  // 4) não está logado: vai para o login, com o returnUrl para voltar aqui
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};