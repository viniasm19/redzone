import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Movimentacao } from '../models/movimentacao.model';

// Abaixo disso = 'estoque baixo'.
export const ESTOQUE_MINIMO = 5;

@Injectable({ providedIn: 'root' })
export class StockService {
  private http = inject(HttpClient);
  private url = `${API_URL}/estoque`;

  // POST /estoque/{id}/entrada.
  entrada(produtoId: number, quantidade: number, observacao?: string) {
    return firstValueFrom(
      this.http.post<Movimentacao>(`${this.url}/${produtoId}/entrada`, { quantidade, observacao })
    );
  }

  // POST /estoque/{id}/saida.
  saida(produtoId: number, quantidade: number, observacao?: string) {
    return firstValueFrom(
      this.http.post<Movimentacao>(`${this.url}/${produtoId}/saida`, { quantidade, observacao })
    );
  }

  // GET /estoque/movimentacoes (histórico).
  movimentacoes(produtoId?: number) {
    const params: Record<string, number> = produtoId ? { produtoId } : {};
    return firstValueFrom(this.http.get<Movimentacao[]>(`${this.url}/movimentacoes`, { params }));
  }
}
