// Um produto, como vem da API.
export interface Produto {
  id: number;
  nome: string;
  preco: number;
  imagem: string;
  categoria: string;
  descricao?: string;
  estoque: number;

  marca?: string | null;
  garantia?: string | null;
  especificacoes?: string | null;
  precoPromocional?: number | null;
}

// Tem preço promocional?
export function emPromocao(produto: Produto): boolean {
  return produto.precoPromocional !== null && produto.precoPromocional !== undefined;
}

// Preço que vale agora (promocional ou normal).
export function precoAtual(produto: Produto): number {
  return emPromocao(produto) ? (produto.precoPromocional as number) : produto.preco;
}
