// Pedido como a API (Spring Boot) devolve.

// Os mesmos valores do enum StatusPedido do back-end.
// Um "union type": a variável só pode ter um destes textos, o TypeScript reclama de qualquer outro.
export type StatusPedido = 'AGUARDANDO_PAGAMENTO' | 'APROVADO' | 'EM_SEPARACAO' | 'ENVIADO' | 'ENTREGUE';

// Texto que aparece na tela para cada status.
// Record<StatusPedido, string> obriga a ter UMA tradução para CADA status (se faltar um, não compila).
export const STATUS_PEDIDO_TEXTO: Record<StatusPedido, string> = {
  AGUARDANDO_PAGAMENTO: 'Aguardando pagamento',
  APROVADO: 'Aprovado',
  EM_SEPARACAO: 'Em separação',
  ENVIADO: 'Enviado',
  ENTREGUE: 'Entregue'
};

// Os mesmos valores do enum FormaPagamento do back-end
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
  precoUnitario: number; // preço no momento da compra
  subtotal: number;
}

export interface Pedido {
  id: number;
  usuarioId: number;
  data: string; // ISO, ex.: 2026-11-08T23:51:00.123
  total: number;
  status: StatusPedido; // pedidos antigos, sem status no banco, chegam como APROVADO
  formaPagamento: FormaPagamento | null; // null enquanto o pedido não foi pago (e nos pedidos antigos)
  itens: ItemPedido[];
}
