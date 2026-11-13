import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Libera a rota só para quem está logado. Se não estiver, manda para o login
// e guarda a página pedida (returnUrl) para voltar nela depois de entrar.
export const authGuard: CanActivateFn = (_route, state) => {
  // 1) pega o AuthService (quem está logado) e o Router (para montar o endereço do.
  const auth = inject(AuthService);
  const router = inject(Router);

  // 2) está logado: libera a tela
  if (auth.isAuthenticated()) {
    return true;
  }

  // 3) não está logado: manda para /login?returnUrl=<a página pedida>.
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};