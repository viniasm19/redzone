import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Produto } from '../../models/produto.model';
import { ESTOQUE_MINIMO } from '../../services/stock.service';

// Uma linha da ficha técnica. "Memória: 8 GB" vira { rotulo: 'Memória', valor: '8 GB' };
// uma linha sem ":" fica só com o valor.
interface Especificacao {
  rotulo: string;
  valor: string;
}

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {

  produto: Produto | null = null;
  especificacoes: Especificacao[] = [];
  estoqueMinimo = ESTOQUE_MINIMO;

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
      next: produto => {
        this.produto = produto;
        this.especificacoes = this.separarEspecificacoes(produto.especificacoes);
      },
      error: () => (this.produto = null)
    });
  }

  // As especificações chegam num texto só, uma por linha. Aqui viram uma lista para a tela.
  // Linhas vazias são descartadas: assim, se não sobrar nada, a seção nem aparece.
  private separarEspecificacoes(texto?: string | null): Especificacao[] {
    if (!texto) {
      return [];
    }
    return texto
      .split(/\r?\n/)
      .map(linha => linha.trim())
      .filter(linha => linha.length > 0)
      .map(linha => {
        const posicao = linha.indexOf(':');
        return posicao > 0
          ? { rotulo: linha.slice(0, posicao).trim(), valor: linha.slice(posicao + 1).trim() }
          : { rotulo: '', valor: linha };
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
    this.router.navigate(['/products']);
  }
}
