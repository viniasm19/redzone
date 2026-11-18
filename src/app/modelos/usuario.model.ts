export interface Usuario {
  id?: number;
  nome: string;
  email: string;
  senha?: string;
}

// Quem está logado (sem senha).
export interface UsuarioLogado {
  id: number;
  nome: string;
  email: string;
}
