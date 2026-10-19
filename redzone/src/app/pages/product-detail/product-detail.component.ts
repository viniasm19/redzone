import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Produto } from '../../models/produto.model';


@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {

  produto: Produto | null = null;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProdutos().subscribe(produtos => {
      this.produto = produtos.find(p => p.id === id) ?? null;
    });
  }

  adicionarAoCarrinho() {
    if (this.produto) {
      this.cartService.adicionarAoCarrinho(this.produto);
      this.router.navigate(['/cart']);
    }
  }

  voltar() {
    this.router.navigate(['/products']);
  }
}