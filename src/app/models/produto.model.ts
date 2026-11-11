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
