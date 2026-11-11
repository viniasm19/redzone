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
// Tela de detalhe de um produto (/product/:id). Mostra imagem, nome, descrição, preço e estoque.
// O HTML espera daqui: produto (null enquanto carrega: a tela só aparece quando ele chega),
// estoqueMinimo, emPromocao e os métodos adicionarAoCarrinho e voltar.
export class ProductDetailComponent implements OnInit {

  produto: Produto | null = null; // o produto da tela (null = ainda não chegou ou não existe)
  estoqueMinimo = ESTOQUE_MINIMO; // até quantas unidades mostrar "Corra! Só restam X"
  emPromocao = emPromocao;        // função do model, exposta para o HTML

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private router: Router
  ) {}

  // Roda quando a tela abre
  ngOnInit() {
    // 1) lê o :id do endereço (/product/7 -> "7") e transforma em número
    const id = Number(this.route.snapshot.paramMap.get('id'));
    // 2) busca o produto na API e guarda quando chegar
    // GET /produtos/{id}: se o produto não existir (404), a tela fica vazia, como antes
    this.productService.getProduto(id).subscribe({
      next: produto => (this.produto = produto),
      error: () => (this.produto = null)
    });
  }

  // Botão "Adicionar ao Carrinho"
  adicionarAoCarrinho() {
    // Só vai para o carrinho se ainda havia unidade para colocar (senão o toast avisa e fica aqui)
    if (this.produto && this.cartService.podeAdicionar(this.produto)) {
      this.cartService.adicionarAoCarrinho(this.produto);
      this.router.navigate(['/cart']);
    } else if (this.produto) {
      this.cartService.adicionarAoCarrinho(this.produto); // mostra o aviso de estoque
    }
  }

  // Botão "← Voltar"
  voltar() {
    this.router.navigate(['/']); // os produtos ficam na tela inicial
  }
}
