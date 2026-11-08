// Usuário como a API (Spring Boot) devolve e como o front envia.
export interface Usuario {
  id?: number; // vazio ao cadastrar: quem gera o id é o banco de dados
  nome: string;
  email: string;
  senha: string;
}
