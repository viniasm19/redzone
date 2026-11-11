import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Movimentacao } from '../models/movimentacao.model';

// Igual ao ESTOQUE_MINIMO do back-end (EstoqueController): até aqui é "estoque baixo"
export const ESTOQUE_MINIMO = 5;

// Conversa com a rota /estoque da API.
@Injectable({ providedIn: 'root' })
export class StockService {
  private http = inject(HttpClient);
  private url = `${API_URL}/estoque`;

  entrada(produtoId: number, quantidade: number, observacao?: string) {
    return firstValueFrom(
      this.http.post<Movimentacao>(`${this.url}/${produtoId}/entrada`, { quantidade, observacao })
    );
  }

  saida(produtoId: number, quantidade: number, observacao?: string) {
    return firstValueFrom(
      this.http.post<Movimentacao>(`${this.url}/${produtoId}/saida`, { quantidade, observacao })
    );
  }

  // Sem produtoId: histórico completo. Com produtoId: só daquele produto.
  movimentacoes(produtoId?: number) {
    const params: Record<string, number> = produtoId ? { produtoId } : {};
    return firstValueFrom(this.http.get<Movimentacao[]>(`${this.url}/movimentacoes`, { params }));
  }
}
