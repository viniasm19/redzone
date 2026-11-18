import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../configuracao/api';
import { FormaPagamento, Pedido } from '../modelos/pedido.model';

@Injectable({ providedIn: 'root' })
export class PagamentoService {
  private http = inject(HttpClient);
  private url = `${API_URL}/pagamentos`;

  // POST /pagamentos: paga o pedido (fictício).
  pagar(pedidoId: number, formaPagamento: FormaPagamento) {
    return firstValueFrom(this.http.post<Pedido>(this.url, { pedidoId, formaPagamento }));
  }
}
