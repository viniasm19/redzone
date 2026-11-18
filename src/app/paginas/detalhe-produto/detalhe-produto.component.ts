import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProdutoService } from '../../servicos/produto.service';
import { CarrinhoService } from '../../servicos/carrinho.service';
import { Produto } from '../../modelos/produto.model';
import { ESTOQUE_MINIMO } from '../../servicos/estoque.service';

@Component({
  selector: 'app-detalhe-produto',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './detalhe-produto.component.html',
  styleUrl: './detalhe-produto.component.scss'
})
// Tela de um produto.
export class DetalheProdutoComponent implements OnInit {

  produto: Produto | null = null;
  estoqueMinimo = ESTOQUE_MINIMO;

  constructor(
    private route: ActivatedRoute,
    private produtoService: ProdutoService,
    private carrinhoService: CarrinhoService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.produtoService.getProduto(id).subscribe({
      next: produto => (this.produto = produto),
      error: () => (this.produto = null)
    });
  }

  // Põe no carrinho e vai para ele.
  adicionarAoCarrinho() {
    if (this.produto && this.carrinhoService.podeAdicionar(this.produto)) {
      this.carrinhoService.adicionarAoCarrinho(this.produto);
      this.router.navigate(['/cart']);
    } else if (this.produto) {
      this.carrinhoService.adicionarAoCarrinho(this.produto);
    }
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
