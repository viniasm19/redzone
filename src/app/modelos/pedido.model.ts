// Formas de pagamento.
export type FormaPagamento = 'CARTAO' | 'PIX';

// Pedido: o front só usa o id e o total.
export interface Pedido {
  id: number;
  total: number;
}
