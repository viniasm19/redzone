// Usuário como a API (Spring Boot) devolve e como o front envia.
export interface Usuario {
  id?: number; // vazio ao cadastrar: quem gera o id é o banco de dados
  nome: string;
  email: string;
  senha?: string; // só é ENVIADO (cadastro, troca de senha); a API nunca devolve a senha
}

// O que a API devolve no login (POST /login): sem a senha, que nunca volta para o navegador.
export interface UsuarioLogado {
  id: number;
  nome: string;
  email: string;
}
