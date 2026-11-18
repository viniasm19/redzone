import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CarrinhoService } from '../../servicos/carrinho.service';
import { AutenticacaoService } from '../../servicos/autenticacao.service';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './carrinho.component.html',
  styleUrl: './carrinho.component.scss',
})
// Tela do carrinho.
export class CarrinhoComponent {
  private carrinhoService = inject(CarrinhoService);
  private auth = inject(AutenticacaoService);
  private router = inject(Router);

  itens = this.carrinhoService.itens;
  total = this.carrinhoService.total;
  quantidadeTotal = this.carrinhoService.quantidadeTotal;


  remover(id: number) {
    this.carrinhoService.removerDoCarrinho(id);
  }

  aumentar(id: number) {
    this.carrinhoService.aumentarQuantidade(id);
  }

  diminuir(id: number) {
    this.carrinhoService.diminuirQuantidade(id);
  }

  limpar() {
    this.carrinhoService.limparCarrinho();
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
