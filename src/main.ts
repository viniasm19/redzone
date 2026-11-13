import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// Ponto de partida do front-end: é o primeiro código que roda no navegador.
// bootstrapApplication "liga" o Angular.
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
