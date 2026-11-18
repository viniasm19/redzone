
// Etapas de um pedido (igual ao back).
export type StatusPedido = 'AGUARDANDO_PAGAMENTO' | 'APROVADO' | 'EM_SEPARACAO' | 'ENVIADO' | 'ENTREGUE';

// Formas de pagamento.
export type FormaPagamento = 'CARTAO' | 'PIX';

export interface ItemPedido {
  produtoId: number;
  nome: string;
  imagem: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
}

// Um pedido, como vem da API.
export interface Pedido {
  id: number;
  usuarioId: number;
  data: string;
  total: number;
  status: StatusPedido;
  formaPagamento: FormaPagamento | null;
  itens: ItemPedido[];
}
