// Uma linha do histórico de estoque.
export type TipoMovimentacao = 'ENTRADA' | 'SAIDA' | 'VENDA';

// Uma linha do histórico de estoque.
export interface Movimentacao {
  id: number;
  produtoId: number;
  produtoNome: string;
  tipo: TipoMovimentacao;
  quantidade: number;
  estoqueApos: number; // quanto ficou no estoque depois dela
  data: string;
  observacao?: string;
}
