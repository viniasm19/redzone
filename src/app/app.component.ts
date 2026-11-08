import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { CartService } from './services/cart.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
// Componente raiz: cabeçalho fixo + a tela da rota atual + aviso (toast) do carrinho.
export class AppComponent {
  title = 'redzone';
  cartService = inject(CartService);
}