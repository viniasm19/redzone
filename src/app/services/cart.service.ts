import { Injectable, computed, effect, signal } from '@angular/core';
import { Produto } from '../models/produto.model';

export interface CartItem {
  produto: Produto;
  quantidade: number;
}

// O carrinho vive só no front (não passa pela API). Fica copiado no localStorage,
// então recarregar a página (F5) não esvazia o carrinho.
// Ele respeita o estoque: não deixa colocar mais unidades do que existem.
// (A palavra final é do back-end, que confere de novo o estoque ao finalizar o pedido.)
@Injectable({ providedIn: 'root' })
export class CartService {
  private static readonly CHAVE = 'redzone.carrinho';

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
    // effect = roda sempre que o carrinho muda: guarda a cópia no navegador
    effect(() => {
      try {
        localStorage.setItem(CartService.CHAVE, JSON.stringify(this.lista()));
      } catch {
        // sem storage: continua só em memória
      }
    });
  }

  // computed = valor calculado a partir de um signal; recalcula sozinho quando o carrinho muda.
  quantidadeTotal = computed(() => this.lista().reduce((soma, i) => soma + i.quantidade, 0));
  total = computed(() => this.lista().reduce((soma, i) => soma + i.produto.preco * i.quantidade, 0));

  // Aviso que aparece por 2,5 segundos
  private _toast = signal<string | null>(null);
  toast = this._toast.asReadonly();
  private timer?: ReturnType<typeof setTimeout>;

  // Quantas unidades deste produto já estão no carrinho
  quantidadeNoCarrinho(id: number) {
    return this.lista().find(i => i.produto.id === id)?.quantidade ?? 0;
  }

  // true se ainda dá para colocar mais uma unidade (sem passar do estoque)
  podeAdicionar(produto: Produto) {
    return this.quantidadeNoCarrinho(produto.id) < produto.estoque;
  }

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

  aumentarQuantidade(id: number) {
    const item = this.lista().find(i => i.produto.id === id);
    if (item) {
      this.adicionarAoCarrinho(item.produto); // mesma checagem de estoque
    }
  }

  diminuirQuantidade(id: number) {
    this.alterarQuantidade(id, -1);
  }

  removerDoCarrinho(id: number) {
    this.lista.update(l => l.filter(i => i.produto.id !== id));
  }

  limparCarrinho() {
    this.lista.set([]);
  }

  private mostrarToast(mensagem: string) {
    this._toast.set(mensagem);
    clearTimeout(this.timer); // se já tinha um aviso, o novo ganha os 2,5 s inteiros
    this.timer = setTimeout(() => this._toast.set(null), 2500);
  }

  // Soma (ou subtrai) a quantidade de um item. Se chegar a 0, o item sai do carrinho.
  // Cria um array novo em vez de alterar o antigo: é isso que faz o signal avisar as telas.
  private alterarQuantidade(id: number, diferenca: number) {
    this.lista.update(l =>
      l
        .map(i => (i.produto.id === id ? { ...i, quantidade: i.quantidade + diferenca } : i))
        .filter(i => i.quantidade > 0)
    );
  }
}
