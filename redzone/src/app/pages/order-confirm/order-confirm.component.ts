import { Component, OnInit } from '@angular/core';
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

  itens: any[] = [];
  total = 0;

  constructor(
    public cartService: CartService,
    private router: Router
  ) { }

  ngOnInit() {
    this.itens = this.cartService.getCartItems()();
    this.total = this.cartService.obterTotal();

    if (this.itens.length === 0) {
      this.router.navigate(['/']);
    }
  }

  confirmar() {
    this.cartService.limparCarrinho();
    this.router.navigate(['/order-success']);
  }

  voltar() {
    this.router.navigate(['/cart']);
  }
}