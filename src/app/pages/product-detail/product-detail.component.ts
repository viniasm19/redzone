import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Produto, emPromocao } from '../../models/produto.model';
import { ESTOQUE_MINIMO } from '../../services/stock.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
// Tela de um produto.
export class ProductDetailComponent implements OnInit {

  produto: Produto | null = null;
  estoqueMinimo = ESTOQUE_MINIMO;
  emPromocao = emPromocao;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProduto(id).subscribe({
      next: produto => (this.produto = produto),
      error: () => (this.produto = null)
    });
  }

  // Põe no carrinho e vai para ele.
  adicionarAoCarrinho() {
    if (this.produto && this.cartService.podeAdicionar(this.produto)) {
      this.cartService.adicionarAoCarrinho(this.produto);
      this.router.navigate(['/cart']);
    } else if (this.produto) {
      this.cartService.adicionarAoCarrinho(this.produto);
    }
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
