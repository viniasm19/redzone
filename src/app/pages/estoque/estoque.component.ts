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
  // Serviço de produtos.
  private productService = inject(ProductService);
  // Serviço de estoque (entrada, saída, histórico).
  private stockService = inject(StockService);

  // Limite de 'estoque baixo' usado na tela.
  estoqueMinimo = ESTOQUE_MINIMO;

  // Todos os produtos carregados.
  produtos: Produto[] = [];
  // Histórico de movimentações mostrado embaixo.
  movimentacoes: Movimentacao[] = [];

  // Texto digitado no campo de busca.
  busca = '';
  // Marca se o filtro 'só estoque baixo' está ligado.
  somenteBaixo = false;

  // Um formulário por produto. Record<número, FormLinha> = "objeto que usa o id do produto como chave".
  forms: Record<number, FormLinha> = {};

  // Histórico: null = de todos os produtos; número = só daquele produto
  historicoDe: Produto | null = null;

  // Mensagem de sucesso.
  mensagem = '';
  // Mensagem de erro.
  erro = '';
  // true enquanto a tela carrega.
  carregando = true;

  // Ao abrir a tela, carrega os dados.
  ngOnInit() {
    this.carregar();
  }

  // Busca produtos e histórico da API.
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

  // Busca o histórico (de todos ou de um produto).
  async carregarHistorico() {
    this.movimentacoes = await this.stockService.movimentacoes(this.historicoDe?.id);
  }

  // ---------- Números do topo ----------

  // Soma de todas as unidades do estoque.
  get totalUnidades() {
    return this.produtos.reduce((soma, p) => soma + p.estoque, 0);
  }

  // Quantos produtos estão com estoque baixo.
  get qtdBaixo() {
    return this.produtos.filter(p => p.estoque > 0 && p.estoque <= ESTOQUE_MINIMO).length;
  }

  // Quantos produtos estão zerados.
  get qtdEsgotados() {
    return this.produtos.filter(p => p.estoque === 0).length;
  }

  // Valor (em R$) de tudo que está parado no estoque
  get valorEmEstoque() {
    return this.produtos.reduce((soma, p) => soma + p.preco * p.estoque, 0);
  }

  // ---------- Lista filtrada ----------

  // Produtos depois da busca e do filtro.
  get produtosFiltrados() {
    const termo = this.busca.trim().toLowerCase();
    return this.produtos
      .filter(p => !termo || p.nome.toLowerCase().includes(termo) || p.categoria.toLowerCase().includes(termo))
      .filter(p => !this.somenteBaixo || p.estoque <= ESTOQUE_MINIMO)
      .sort((a, b) => a.estoque - b.estoque); // os que mais precisam de reposição aparecem primeiro
  }

  // Diz se o produto está zerado, baixo ou ok.
  status(p: Produto): 'zero' | 'baixo' | 'ok' {
    if (p.estoque === 0) return 'zero';
    if (p.estoque <= ESTOQUE_MINIMO) return 'baixo';
    return 'ok';
  }

  // ---------- Entrada e saída ----------

  // Registra entrada ou saída de um produto.
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

  // Mostra o histórico de um produto (ou de todos).
  async verHistorico(produto: Produto | null) {
    this.historicoDe = produto;
    await this.carregarHistorico();
  }

  // Nome bonito de cada tipo de movimentação.
  rotuloTipo(tipo: TipoMovimentacao) {
    return { ENTRADA: '➕ Entrada', SAIDA: '➖ Saída', VENDA: '🛒 Venda' }[tipo];
  }

  // Ajuda o Angular a reaproveitar as linhas da lista.
  trackById(_: number, item: { id: number }) {
    return item.id;
  }
}
