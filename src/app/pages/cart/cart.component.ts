import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { emPromocao, precoAtual } from '../../models/produto.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
// Tela do carrinho (/cart). O HTML espera daqui: itens.
// precoAtual e emPromocao (para o preço de cada item) e os métodos dos botões
export class CartComponent {
  // inject(): pede ao Angular os services prontos
  private cartService = inject(CartService);
  private auth = inject(AuthService);
  private router = inject(Router);

  // Valores que a tela mostra (o cálculo de total e quantidade fica no CartService)
  itens = this.cartService.itens;
  total = this.cartService.total;
  quantidadeTotal = this.cartService.quantidadeTotal;

  // Funções do model.
  precoAtual = precoAtual;
  emPromocao = emPromocao;

  // Os 4 métodos abaixo só repassam o clique para o CartService.
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
  // logado     -> Checkout (/order-confirm) -> Home | Área do Usuário
  finalizarCompra() {
    // 1) logado: vai direto ao checkout
    if (this.auth.isAuthenticated()) {
      this.router.navigate(['/order-confirm']);
    } else {
      // 2) não logado: vai ao login com ?returnUrl=/order-confirm (o login traz de.
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/order-confirm' } });
    }
  }
}
