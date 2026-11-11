import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent {
  private cartService = inject(CartService);
  private auth = inject(AuthService);
  private router = inject(Router);

  // Valores que a tela mostra (o cálculo de total e quantidade fica no CartService)
  itens = this.cartService.itens;
  total = this.cartService.total;
  quantidadeTotal = this.cartService.quantidadeTotal;

  remover(id: number) {
    this.cartService.removerDoCarrinho(id);
  }

  aumentar(id: number) {
    this.cartService.aumentarQuantidade(id);
  }

  diminuir(id: number) {
    this.cartService.diminuirQuantidade(id);
  }

  limpar() {
    this.cartService.limparCarrinho();
  }

  // Fluxo de compra:
  //   logado     -> Checkout (/order-confirm) -> Home | Área do Usuário
  //   não logado -> Login -> (volta sozinho para o) Checkout -> Home | Área do Usuário
  // O returnUrl diz ao login para onde voltar depois de entrar (ou de se cadastrar).
  // O authGuard continua protegendo /order-confirm caso alguém digite o endereço direto.
  finalizarCompra() {
    if (this.auth.isAuthenticated()) {
      this.router.navigate(['/order-confirm']);
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/order-confirm' } });
    }
  }
}
