import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Produto } from '../../models/produto.model';
import { Movimentacao, TipoMovimentacao } from '../../models/movimentacao.model';
import { ProductService } from '../../services/product.service';
import { ESTOQUE_MINIMO, StockService } from '../../services/stock.service';

// O que o admin está digitando na linha de cada produto (quantidade e observação)
interface FormLinha {
  quantidade: number | null;
  observacao: string;
}

// Tela de controle de estoque (rota /estoque, só para o administrador).
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

  estoqueMinimo = ESTOQUE_MINIMO;

  produtos: Produto[] = [];
  movimentacoes: Movimentacao[] = [];

  busca = '';
  somenteBaixo = false;

  // Um formulário por produto. Record<número, FormLinha> = "objeto que usa o id do produto como chave".
  forms: Record<number, FormLinha> = {};

  // Histórico: null = de todos os produtos; número = só daquele produto
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
        this.forms[p.id] ??= { quantidade: null, observacao: '' }; // ??= só cria se ainda não existir
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

  // ---------- Números do topo ----------

  get totalUnidades() {
    return this.produtos.reduce((soma, p) => soma + p.estoque, 0);
  }

  get qtdBaixo() {
    return this.produtos.filter(p => p.estoque > 0 && p.estoque <= ESTOQUE_MINIMO).length;
  }

  get qtdEsgotados() {
    return this.produtos.filter(p => p.estoque === 0).length;
  }

  // Valor (em R$) de tudo que está parado no estoque
  get valorEmEstoque() {
    return this.produtos.reduce((soma, p) => soma + p.preco * p.estoque, 0);
  }

  // ---------- Lista filtrada ----------

  get produtosFiltrados() {
    const termo = this.busca.trim().toLowerCase();
    return this.produtos
      .filter(p => !termo || p.nome.toLowerCase().includes(termo) || p.categoria.toLowerCase().includes(termo))
      .filter(p => !this.somenteBaixo || p.estoque <= ESTOQUE_MINIMO)
      .sort((a, b) => a.estoque - b.estoque); // os que mais precisam de reposição aparecem primeiro
  }

  status(p: Produto): 'zero' | 'baixo' | 'ok' {
    if (p.estoque === 0) return 'zero';
    if (p.estoque <= ESTOQUE_MINIMO) return 'baixo';
    return 'ok';
  }

  // ---------- Entrada e saída ----------

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

      produto.estoque = mov.estoqueApos; // atualiza a linha na hora, sem recarregar tudo
      this.forms[produto.id] = { quantidade: null, observacao: '' };
      this.mensagem = `${tipo === 'ENTRADA' ? '➕ Entrada' : '➖ Saída'} de ${quantidade} un. em "${produto.nome}". Estoque agora: ${mov.estoqueApos}.`;
      await this.carregarHistorico();
    } catch (e) {
      // 409 = tentou tirar mais do que tem. A API manda a explicação em "mensagem".
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
