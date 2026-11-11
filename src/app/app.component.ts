import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { CartService } from './services/cart.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, FooterComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
// Componente raiz: cabeçalho fixo + a tela da rota atual + rodapé + aviso (toast) do carrinho.
// O HTML (app.component.html) espera daqui: o cartService, para ler o aviso (toast) do carrinho.
export class AppComponent {
  title = 'redzone';               // nome do app (sobrou do projeto inicial do Angular; o HTML não usa)
  cartService = inject(CartService); // inject() pede ao Angular o CartService (o mesmo objeto para o app todo)
}