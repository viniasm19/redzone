import { Injectable, computed, effect, signal } from '@angular/core';
import { Produto, precoAtual } from '../models/produto.model';

export interface CartItem {
  produto: Produto;
  quantidade: number;
}

// O carrinho vive só no front (não passa pela API). Fica copiado no localStorage,
// então recarregar a página (F5) não esvazia o carrinho.
@Injectable({ providedIn: 'root' })
export class CartService {
  // Nome da "gaveta" do localStorage onde o carrinho fica guardado
  private static readonly CHAVE = 'redzone.carrinho';

  // signal = valor que o Angular "observa": quando muda.
  // "lista" é privada (só este service altera); as telas leem "itens".
  private lista = signal<CartItem[]>(CartService.lerSalvo());
  itens = this.lista.asReadonly();

  // Lê o carrinho guardado no navegador (roda uma vez, quando o app abre).
  private static lerSalvo(): CartItem[] {
    try {
      // 1) pega o texto guardado e transforma em objeto (ou lista vazia.
      const texto = localStorage.getItem(CartService.CHAVE);
      const dados = texto ? JSON.parse(texto) : [];
      // 2) só aceita se for mesmo uma lista (proteção contra um valor estranho no.
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

  // computed = valor calculado a partir de um signal.
  quantidadeTotal = computed(() => this.lista().reduce((soma, i) => soma + i.quantidade, 0));
  // Usa o preço atual (promocional, se houver). É só uma prévia.
  total = computed(() => this.lista().reduce((soma, i) => soma + precoAtual(i.produto) * i.quantidade, 0));

  // Aviso que aparece por 2,5 segundos
  private _toast = signal<string | null>(null);
  toast = this._toast.asReadonly();
  private timer?: ReturnType<typeof setTimeout>;

  // Quantas unidades deste produto já estão no carrinho
  // Recebe: o id do produto. Devolve.
  quantidadeNoCarrinho(id: number) {
    return this.lista().find(i => i.produto.id === id)?.quantidade ?? 0;
  }

  // true se ainda dá para colocar mais uma unidade (sem passar do estoque)
  podeAdicionar(produto: Produto) {
    return this.quantidadeNoCarrinho(produto.id) < produto.estoque;
  }

  // Recebe: o produto clicado. Não devolve nada (muda o carrinho e mostra um aviso).
  adicionarAoCarrinho(produto: Produto) {
    // 1) sem estoque para mais uma unidade: só avisa e para aqui
    if (!this.podeAdicionar(produto)) {
      this.mostrarToast(
        produto.estoque === 0
          ? `⚠️ ${produto.nome} está esgotado.`
          : `⚠️ Só temos ${produto.estoque} unidade(s) de ${produto.nome}.`
      );
      return;
    }

    // 2) .some(...) = "algum item da lista é este produto?"
    const jaEstaNoCarrinho = this.lista().some(i => i.produto.id === produto.id);

    // 3) já está: soma 1 na quantidade. Não está: entra na lista com quantidade 1
    if (jaEstaNoCarrinho) {
      this.alterarQuantidade(produto.id, +1);
    } else {
      this.lista.update(l => [...l, { produto, quantidade: 1 }]);
    }

    // 4) avisa que deu certo
    this.mostrarToast(`✅ ${produto.nome} adicionado ao carrinho!`);
  }

  // Botão "+" do carrinho. Recebe: o id do produto.
  aumentarQuantidade(id: number) {
    const item = this.lista().find(i => i.produto.id === id);
    if (item) {
      this.adicionarAoCarrinho(item.produto); // mesma checagem de estoque
    }
  }

  // Botão "−" do carrinho (se chegar a 0, o item sai)
  diminuirQuantidade(id: number) {
    this.alterarQuantidade(id, -1);
  }

  // Botão "Remover": .filter mantém todos os itens, MENOS o deste produto
  removerDoCarrinho(id: number) {
    this.lista.update(l => l.filter(i => i.produto.id !== id));
  }

  // Esvazia o carrinho (botão "Limpar" e depois que o pedido é criado)
  limparCarrinho() {
    this.lista.set([]);
  }

  // Mostra um aviso por 2,5 segundos. Recebe: o texto do aviso.
  private mostrarToast(mensagem: string) {
    // 1) coloca o texto no signal: o app.component.html mostra o aviso
    this._toast.set(mensagem);
    clearTimeout(this.timer); // se já tinha um aviso, o novo ganha os 2,5 s inteiros
    // 2) daqui a 2500 ms, apaga o texto e o aviso some
    this.timer = setTimeout(() => this._toast.set(null), 2500);
  }

  // Soma (ou subtrai) a quantidade de um item. Se chegar a 0.
  // Cria um array novo em vez de alterar o antigo.
  private alterarQuantidade(id: number, diferenca: number) {
    this.lista.update(l =>
      l
        .map(i => (i.produto.id === id ? { ...i, quantidade: i.quantidade + diferenca } : i))
        .filter(i => i.quantidade > 0)
    );
  }
}
