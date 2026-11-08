// Produto como a API (Spring Boot) devolve e como o front envia.
export interface Produto {
  id: number;
  nome: string;
  preco: number;
  imagem: string; // caminho em /public (ex.: /img/produtos/headset.jpg) ou link
  categoria: string;
  descricao?: string;
}
