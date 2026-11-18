import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AutenticacaoService } from '../servicos/autenticacao.service';

// Guard: deixa entrar só quem está logado.
export const autenticacaoGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AutenticacaoService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }

  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};