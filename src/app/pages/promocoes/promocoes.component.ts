import { Component, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProductItemComponent } from '../../components/product-item/product-item.component';
import { Produto } from '../../models/produto.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-promocoes',
  standalone: true,
  imports: [ProductItemComponent, RouterModule],
  templateUrl: './promocoes.component.html',
  styleUrl: './promocoes.component.scss'
})
// Página de promoções.
export class PromocoesComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);

  produtos: Produto[] = [];
  carregando = true;
  erro = '';

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

  adicionarAoCarrinho(produto: Produto) {
    this.cartService.adicionarAoCarrinho(produto);
  }
}
