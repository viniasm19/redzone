// Pedido como a API (Spring Boot) devolve.
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
  itens: ItemPedido[];
}
