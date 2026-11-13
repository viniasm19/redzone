import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { FormaPagamento, Pedido } from '../models/pedido.model';

// Conversa com a rota /pagamentos da API (pagamento FICTÍCIO: sempre aprova).
@Injectable({ providedIn: 'root' })
export class PaymentService {
  private http = inject(HttpClient);
  // Endereço de /pagamentos.
  private url = `${API_URL}/pagamentos`;

  // Manda SÓ o id do pedido e a forma de pagamento.
  // Número, validade e CVV do cartão ficam na tela e nunca são enviados à API.
  // Recebe: o id do pedido e 'CARTAO' ou 'PIX'. Devolve: o pedido atualizado (status APROVADO).
  pagar(pedidoId: number, formaPagamento: FormaPagamento) {
    return firstValueFrom(this.http.post<Pedido>(this.url, { pedidoId, formaPagamento }));
  }
}
