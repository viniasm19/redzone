import { Component, computed } from '@angular/core';
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

  constructor(
    public cartService: CartService,
    private router: Router
  ) {}

  get itens() {
    return this.cartService.getCartItems();
  }

  quantidadeTotal = computed(() =>
    this.cartService.getCartItems()().reduce(
      (total, item) => total + item.quantidade,
      0
    )
  );

  total = computed(() =>
    this.cartService.getCartItems()().reduce(
      (total, item) =>
        total + item.produto.preco * item.quantidade,
      0
    )
  );

  remover(id: number) {
    this.cartService.removerDoCarrinho(id);
  }

  limpar() {
    this.cartService.limparCarrinho();
  }

  finalizarCompra() {
    this.router.navigate(['/order-confirm']);
  }
}
