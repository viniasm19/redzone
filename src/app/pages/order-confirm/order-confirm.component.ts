import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-order-confirm',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.scss'
})
export class OrderConfirmComponent implements OnInit {
  private cartService = inject(CartService);
  private router = inject(Router);

  // "Foto" do carrinho ao entrar na tela: continua na tela mesmo depois de limpar o carrinho
  itens = this.cartService.itens();
  total = this.cartService.total();

  ngOnInit() {
    if (this.itens.length === 0) {
      this.router.navigate(['/']); // sem itens não há o que confirmar
    }
  }

  confirmar() {
    this.cartService.limparCarrinho(); // o pedido não é salvo na API: este projeto só simula a compra
    this.router.navigate(['/order-success']);
  }

  voltar() {
    this.router.navigate(['/cart']);
  }
}
