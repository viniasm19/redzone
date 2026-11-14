import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Produto } from '../../models/produto.model';
import { Movimentacao, TipoMovimentacao } from '../../models/movimentacao.model';
import { ProductService } from '../../services/product.service';
import { ESTOQUE_MINIMO, StockService } from '../../services/stock.service';

interface FormLinha {
  quantidade: number | null;
  observacao: string;
}

@Component({
  selector: 'app-estoque',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './estoque.component.html',
  styleUrl: './estoque.component.scss'
})
export class EstoqueComponent implements OnInit {
  private productService = inject(ProductService);
  private stockService = inject(StockService);

  // Limite de 'estoque baixo' usado na tela.
  estoqueMinimo = ESTOQUE_MINIMO;

  produtos: Produto[] = [];
  movimentacoes: Movimentacao[] = [];

  busca = '';
  somenteBaixo = false;

  forms: Record<number, FormLinha> = {};

  historicoDe: Produto | null = null;

  mensagem = '';
  erro = '';
  carregando = true;

  ngOnInit() {
    this.carregar();
  }

  async carregar() {
    this.carregando = true;
    try {
      this.produtos = await firstValueFrom(this.productService.getProdutos());
      for (const p of this.produtos) {
        this.forms[p.id] ??= { quantidade: null, observacao: '' };
      }
      await this.carregarHistorico();
    } catch {
      this.erro = 'Não foi possível carregar o estoque. Confira se a API está rodando.';
    } finally {
      this.carregando = false;
    }
  }

  async carregarHistorico() {
    this.movimentacoes = await this.stockService.movimentacoes(this.historicoDe?.id);
  }


  get totalUnidades() {
    return this.produtos.reduce((soma, p) => soma + p.estoque, 0);
  }

  get qtdBaixo() {
    return this.produtos.filter(p => p.estoque > 0 && p.estoque <= ESTOQUE_MINIMO).length;
  }

  get qtdEsgotados() {
    return this.produtos.filter(p => p.estoque === 0).length;
  }

  get valorEmEstoque() {
    return this.produtos.reduce((soma, p) => soma + p.preco * p.estoque, 0);
  }


  get produtosFiltrados() {
    const termo = this.busca.trim().toLowerCase();
    return this.produtos
      .filter(p => !termo || p.nome.toLowerCase().includes(termo) || p.categoria.toLowerCase().includes(termo))
      .filter(p => !this.somenteBaixo || p.estoque <= ESTOQUE_MINIMO)
      .sort((a, b) => a.estoque - b.estoque);
  }

  status(p: Produto): 'zero' | 'baixo' | 'ok' {
    if (p.estoque === 0) return 'zero';
    if (p.estoque <= ESTOQUE_MINIMO) return 'baixo';
    return 'ok';
  }


  async movimentar(produto: Produto, tipo: 'ENTRADA' | 'SAIDA') {
    this.mensagem = '';
    this.erro = '';

    const form = this.forms[produto.id];
    const quantidade = Number(form.quantidade);

    if (!Number.isInteger(quantidade) || quantidade <= 0) {
      this.erro = `Informe uma quantidade inteira maior que zero para "${produto.nome}".`;
      return;
    }

    try {
      const obs = form.observacao.trim() || undefined;
      const mov = tipo === 'ENTRADA'
        ? await this.stockService.entrada(produto.id, quantidade, obs)
        : await this.stockService.saida(produto.id, quantidade, obs);

      produto.estoque = mov.estoqueApos;
      this.forms[produto.id] = { quantidade: null, observacao: '' };
      this.mensagem = `${tipo === 'ENTRADA' ? '➕ Entrada' : '➖ Saída'} de ${quantidade} un. em "${produto.nome}". Estoque agora: ${mov.estoqueApos}.`;
      await this.carregarHistorico();
    } catch (e) {
      this.erro = e instanceof HttpErrorResponse && e.error?.mensagem
        ? e.error.mensagem
        : 'Não foi possível registrar a movimentação.';
    }
  }

  async verHistorico(produto: Produto | null) {
    this.historicoDe = produto;
    await this.carregarHistorico();
  }

  rotuloTipo(tipo: TipoMovimentacao) {
    return { ENTRADA: '➕ Entrada', SAIDA: '➖ Saída', VENDA: '🛒 Venda' }[tipo];
  }

  trackById(_: number, item: { id: number }) {
    return item.id;
  }
}
