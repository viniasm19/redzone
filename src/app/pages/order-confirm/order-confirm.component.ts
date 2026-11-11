import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';
import { precoAtual } from '../../models/produto.model';

@Component({
  selector: 'app-order-confirm',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.scss'
})
export class OrderConfirmComponent implements OnInit {
  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private orderService = inject(OrderService);
  private router = inject(Router);

  // "Foto" do carrinho ao entrar na tela: continua na tela mesmo depois de limpar o carrinho
  itens = this.cartService.itens();
  total = this.cartService.total();

  precoAtual = precoAtual; // para o HTML calcular o subtotal com o preço promocional

  enviando = false; // true enquanto espera a API: desabilita o botão (evita pedido duplicado)
  erro = '';

  ngOnInit() {
    if (this.itens.length === 0) {
      this.router.navigate(['/']); // sem itens não há o que confirmar
    }
  }

  async confirmar() {
    const usuario = this.authService.usuarioLogado();
    if (!usuario?.id || this.enviando) {
      return;
    }

    this.enviando = true;
    this.erro = '';

    try {
      await this.orderService.criarPedido(usuario.id, this.itens); // salva o pedido no banco
      this.cartService.limparCarrinho(); // só limpa o carrinho depois que a API confirmou
      this.router.navigate(['/order-success']);
    } catch (e) {
      // 409 = algum produto acabou enquanto a pessoa comprava. O back-end diz qual na "mensagem".
      if (e instanceof HttpErrorResponse && e.status === 409 && e.error?.mensagem) {
        this.erro = `${e.error.mensagem} Volte ao carrinho e ajuste a quantidade.`;
      } else {
        this.erro = 'Não foi possível finalizar o pedido. Tente novamente.';
      }
      this.enviando = false;
    }
  }

  voltar() {
    this.router.navigate(['/cart']);
  }
}
