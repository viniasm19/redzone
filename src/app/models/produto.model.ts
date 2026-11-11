// Produto como a API (Spring Boot) devolve e como o front envia.
export interface Produto {
  id: number;
  nome: string;
  preco: number;
  imagem: string; // caminho em /public (ex.: /img/produtos/headset.jpg) ou link
  categoria: string;
  descricao?: string;
  estoque: number; // unidades disponíveis (só muda pela tela de Estoque ou quando alguém compra)

  // Campos opcionais: produtos antigos vêm com null (por isso o "| null")
  marca?: string | null;
  garantia?: string | null;        // ex.: "12 meses"
  especificacoes?: string | null;  // uma especificação por linha (ex.: "Conexão: USB")
  precoPromocional?: number | null; // preenchido = produto em promoção
}

// Em promoção = tem precoPromocional preenchido (mesma regra do back-end).
export function emPromocao(produto: Produto): boolean {
  return produto.precoPromocional !== null && produto.precoPromocional !== undefined;
}

// Quanto o produto custa agora: o promocional, se houver, senão o normal.
// É a mesma conta que o back-end faz ao fechar o pedido (lá, com o preço do banco).
export function precoAtual(produto: Produto): number {
  return emPromocao(produto) ? (produto.precoPromocional as number) : produto.preco;
}
