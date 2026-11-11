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
    // GET /produtos/{id}: se o produto não existir (404), a tela fica vazia, como antes
    this.productService.getProduto(id).subscribe({
      next: produto => (this.produto = produto),
      error: () => (this.produto = null)
    });
  }

  adicionarAoCarrinho() {
    // Só vai para o carrinho se ainda havia unidade para colocar (senão o toast avisa e fica aqui)
    if (this.produto && this.cartService.podeAdicionar(this.produto)) {
      this.cartService.adicionarAoCarrinho(this.produto);
      this.router.navigate(['/cart']);
    } else if (this.produto) {
      this.cartService.adicionarAoCarrinho(this.produto); // mostra o aviso de estoque
    }
  }

  voltar() {
    this.router.navigate(['/']); // os produtos ficam na tela inicial
  }
}
