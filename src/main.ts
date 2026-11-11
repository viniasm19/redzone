import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// Ponto de partida do front-end: é o primeiro código que roda no navegador.
// bootstrapApplication "liga" o Angular: desenha o AppComponent (a tag <app-root> do index.html)
// usando as configurações do appConfig (rotas e HttpClient).
// .catch: se algo der errado ao iniciar, mostra o erro no console do navegador (F12).
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
