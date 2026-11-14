import { Injectable, computed, effect, signal } from '@angular/core';
import { Produto, precoAtual } from '../models/produto.model';

export interface CartItem {
  produto: Produto;
  quantidade: number;
}

@Injectable({ providedIn: 'root' })
// Carrinho. Fica salvo no localStorage.
export class CartService {
  // Chave do localStorage (carrinho).
  private static readonly CHAVE = 'redzone.carrinho';

  // Lista de itens. Signal: atualiza as telas sozinho.
  private lista = signal<CartItem[]>(CartService.lerSalvo());
  itens = this.lista.asReadonly();

  private static lerSalvo(): CartItem[] {
    try {
      const texto = localStorage.getItem(CartService.CHAVE);
      const dados = texto ? JSON.parse(texto) : [];
      return Array.isArray(dados) ? dados : [];
    } catch {
      return [];
    }
  }

  constructor() {
    effect(() => {
      try {
        localStorage.setItem(CartService.CHAVE, JSON.stringify(this.lista()));
      } catch {
      }
    });
  }

  // Total de unidades (contador do menu).
  quantidadeTotal = computed(() => this.lista().reduce((soma, i) => soma + i.quantidade, 0));
  total = computed(() => this.lista().reduce((soma, i) => soma + precoAtual(i.produto) * i.quantidade, 0));

  private _toast = signal<string | null>(null);
  toast = this._toast.asReadonly();
  private timer?: ReturnType<typeof setTimeout>;

  // Quantas unidades deste produto há no carrinho.
  quantidadeNoCarrinho(id: number) {
    return this.lista().find(i => i.produto.id === id)?.quantidade ?? 0;
  }

  // Ainda cabe mais uma unidade (estoque)?
  podeAdicionar(produto: Produto) {
    return this.quantidadeNoCarrinho(produto.id) < produto.estoque;
  }

  // Adiciona um produto, respeitando o estoque.
  adicionarAoCarrinho(produto: Produto) {
    if (!this.podeAdicionar(produto)) {
      this.mostrarToast(
        produto.estoque === 0
          ? `⚠️ ${produto.nome} está esgotado.`
          : `⚠️ Só temos ${produto.estoque} unidade(s) de ${produto.nome}.`
      );
      return;
    }

    const jaEstaNoCarrinho = this.lista().some(i => i.produto.id === produto.id);

    if (jaEstaNoCarrinho) {
      this.alterarQuantidade(produto.id, +1);
    } else {
      this.lista.update(l => [...l, { produto, quantidade: 1 }]);
    }

    this.mostrarToast(`✅ ${produto.nome} adicionado ao carrinho!`);
  }

  // Botão +.
  aumentarQuantidade(id: number) {
    const item = this.lista().find(i => i.produto.id === id);
    if (item) {
      this.adicionarAoCarrinho(item.produto);
    }
  }

  // Botão −.
  diminuirQuantidade(id: number) {
    this.alterarQuantidade(id, -1);
  }

  // Remove o produto do carrinho.
  removerDoCarrinho(id: number) {
    this.lista.update(l => l.filter(i => i.produto.id !== id));
  }

  // Esvazia o carrinho.
  limparCarrinho() {
    this.lista.set([]);
  }

  // Mostra um aviso por 2,5 segundos.
  private mostrarToast(mensagem: string) {
    this._toast.set(mensagem);
    clearTimeout(this.timer);
    this.timer = setTimeout(() => this._toast.set(null), 2500);
  }

  private alterarQuantidade(id: number, diferenca: number) {
    this.lista.update(l =>
      l
        .map(i => (i.produto.id === id ? { ...i, quantidade: i.quantidade + diferenca } : i))
        .filter(i => i.quantidade > 0)
    );
  }
}
