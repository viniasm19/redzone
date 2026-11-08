import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent {
  private cartService = inject(CartService);
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

  finalizarCompra() {
    this.router.navigate(['/order-confirm']); // rota protegida: exige login (authGuard)
  }
}
