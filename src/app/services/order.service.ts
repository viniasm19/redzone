import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Pedido } from '../models/pedido.model';
import { CartItem } from './cart.service';

// Conversa com a rota /pedidos da API.
// firstValueFrom(...) transforma a resposta da API numa Promise: assim quem chama pode usar
// "await orderService.criarPedido(...)" e esperar a resposta na própria linha.
@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private url = `${API_URL}/pedidos`;

  // Manda só ids e quantidades: o preço e o total quem calcula é o back-end.
  // Recebe: o id de quem compra e os itens do carrinho. Devolve: o pedido salvo (com o total oficial).
  criarPedido(usuarioId: number, itens: CartItem[]) {
    // 1) monta o corpo do POST: para cada item do carrinho, só o id do produto e a quantidade
    const corpo = {
      usuarioId,
      itens: itens.map(i => ({ produtoId: i.produto.id, quantidade: i.quantidade }))
    };
    // 2) POST /pedidos e espera a resposta (201 com o pedido, ou erro 404/409)
    return firstValueFrom(this.http.post<Pedido>(this.url, corpo));
  }

  // GET /pedidos/usuario/{id}: os pedidos de um usuário, do mais novo para o mais antigo (Minha Área)
  listarPorUsuario(usuarioId: number) {
    return firstValueFrom(this.http.get<Pedido[]>(`${this.url}/usuario/${usuarioId}`));
  }
}
