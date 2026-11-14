
// Etapas de um pedido (igual ao back).
export type StatusPedido = 'AGUARDANDO_PAGAMENTO' | 'APROVADO' | 'EM_SEPARACAO' | 'ENVIADO' | 'ENTREGUE';

// Texto mostrado para cada status.
export const STATUS_PEDIDO_TEXTO: Record<StatusPedido, string> = {
  AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
  APROVADO: 'Aprovado',
  EM_SEPARACAO: 'Em separação',
  ENVIADO: 'Enviado',
  ENTREGUE: 'Entregue'
};

// Formas de pagamento.
export type FormaPagamento = 'CARTAO' | 'PIX';

export const FORMA_PAGAMENTO_TEXTO: Record<FormaPagamento, string> = {
  CARTAO: 'Cartão de crédito',
  PIX: 'Pix'
};

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
