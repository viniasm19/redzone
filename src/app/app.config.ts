import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';

// Configuração geral do app, usada no main.ts.
// "providers" = ferramentas que ficam disponíveis para TODOS os componentes e.
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),  // liga o sistema de rotas (endereço da barra -> tela), com o mapa do app.routes.ts
    provideHttpClient()     // liga o HttpClient, que os services usam para chamar a API (GET, POST...)
  ]
};