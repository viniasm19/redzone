import { Injectable, signal, computed } from '@angular/core';
import { Produto } from '../models/produto.model';

export type { Produto };



export interface CartItem {
  produto: Produto;
  quantidade: number;
}


@Injectable({
  providedIn: 'root'
})
export class CartService {

  private itensCarrinho = signal<CartItem[]>([]);
  private _toast = signal<string | null>(null);


  toast = this._toast.asReadonly();

  getCartItems() {
    return this.itensCarrinho;
  }

  adicionarAoCarrinho(produto: Produto) {
    const itensAtuais = this.itensCarrinho();

    const itemExistente = itensAtuais.find(
      item => item.produto.id === produto.id
    );

    if (itemExistente) {

      const atualizado = itensAtuais.map(item =>
        item.produto.id === produto.id
          ? {
            ...item,
            quantidade: item.quantidade + 1
          }
          : item
      );

      this.itensCarrinho.set(atualizado);

    } else {
      this.itensCarrinho.set([
        ...itensAtuais,
        { produto, quantidade: 1 }
      ]);
    }

    this._toast.set(`✅ ${produto.nome} adicionado ao carrinho!`);
    setTimeout(() => this._toast.set(null), 2500);

  }

  removerDoCarrinho(produtoId: number) {
    this.itensCarrinho.set(
      this.itensCarrinho().filter(
        item => item.produto.id !== produtoId
      )
    );
  }


  limparCarrinho() {
    this.itensCarrinho.set([]);
  }

  obterTotal() {
    return this.itensCarrinho().reduce(
      (total, item) =>
        total + item.produto.preco * item.quantidade,
      0
    );
  }

 obterQuantidadeTotal = computed(() =>
  this.itensCarrinho().reduce(
    (total, item) => total + item.quantidade,
    0
  )
);
}