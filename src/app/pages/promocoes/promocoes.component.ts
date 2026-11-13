import { Component, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProductItemComponent } from '../../components/product-item/product-item.component';
import { Produto } from '../../models/produto.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';

// Página de promoções (rota /promocoes).
// Não existe rota nova no back: é o mesmo GET /produtos.
@Component({
  selector: 'app-promocoes',
  standalone: true,
  imports: [ProductItemComponent, RouterModule],
  templateUrl: './promocoes.component.html',
  styleUrl: './promocoes.component.scss'
})
export class PromocoesComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);

  // O HTML espera daqui: produtos.
  produtos: Produto[] = [];  // produtos em promoção
  carregando = true;         // true até a resposta da API chegar
  erro = '';                 // aviso se a API falhar

  // Roda quando a tela abre: GET /produtos?promocao=true
  ngOnInit() {
    this.productService.getPromocoes().subscribe({
      next: dados => {
        this.produtos = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Não foi possível carregar as promoções. Confira se a API está rodando.';
        this.carregando = false;
      }
    });
  }

  // O card avisou que o botão "Adicionar" foi clicado
  adicionarAoCarrinho(produto: Produto) {
    this.cartService.adicionarAoCarrinho(produto);
  }
}
