import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../configuracao/api';
import { Pedido } from '../modelos/pedido.model';
import { CartItem } from './carrinho.service';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private http = inject(HttpClient);
  private url = `${API_URL}/pedidos`;

  // POST /pedidos: envia só ids e quantidades.
  criarPedido(usuarioId: number, itens: CartItem[]) {
    const corpo = {
      usuarioId,
      itens: itens.map(i => ({ produtoId: i.produto.id, quantidade: i.quantidade }))
    };
    return firstValueFrom(this.http.post<Pedido>(this.url, corpo));
  }
}
