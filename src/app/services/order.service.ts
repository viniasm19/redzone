import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Pedido } from '../models/pedido.model';
import { CartItem } from './cart.service';

// Conversa com a rota /pedidos da API.
@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private url = `${API_URL}/pedidos`;

  // Manda só ids e quantidades: o preço e o total quem calcula é o back-end.
  criarPedido(usuarioId: number, itens: CartItem[]) {
    const corpo = {
      usuarioId,
      itens: itens.map(i => ({ produtoId: i.produto.id, quantidade: i.quantidade }))
    };
    return firstValueFrom(this.http.post<Pedido>(this.url, corpo));
  }

  listarPorUsuario(usuarioId: number) {
    return firstValueFrom(this.http.get<Pedido[]>(`${this.url}/usuario/${usuarioId}`));
  }
}
