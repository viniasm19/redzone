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
// Tela do carrinho.
export class CartComponent {
  private cartService = inject(CartService);
  private auth = inject(AuthService);
  private router = inject(Router);

  itens = this.cartService.itens;
  total = this.cartService.total;
  quantidadeTotal = this.cartService.quantidadeTotal;

  precoAtual = precoAtual;
  emPromocao = emPromocao;

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

  // Vai para o checkout ou para o login antes.
  finalizarCompra() {
    if (this.auth.isAuthenticated()) {
      this.router.navigate(['/order-confirm']);
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/order-confirm' } });
    }
  }
}
