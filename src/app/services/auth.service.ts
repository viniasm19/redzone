import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Usuario } from '../models/usuario.model';

// "Lucas@Email.com " e "lucas@email.com" têm que ser o mesmo usuário.
const normalizar = (email: string) => email.trim().toLowerCase();

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private url = `${API_URL}/usuarios`;

  // Quem está logado. Fica só na memória: recarregar a página desloga.
  // signal = valor que o Angular observa; quando muda, as telas que o usam atualizam sozinhas.
  private _usuarioLogado = signal<Usuario | null>(null);
  usuarioLogado = this._usuarioLogado.asReadonly();

  isAuthenticated() {
    return this._usuarioLogado() !== null;
  }

  // O administrador é quem tem este e-mail (regra simples, só para o trabalho acadêmico).
  isAdmin() {
    return this._usuarioLogado()?.email === 'admin@redzone.com';
  }

  logout() {
    this._usuarioLogado.set(null);
  }

  // ---------- Login e cadastro ----------

  // Busca os usuários na API e procura um com esse e-mail e senha.
  async login(dados: { email: string; senha: string }) {
    const email = normalizar(dados.email);
    const usuarios = await this.listarUsuarios();

    const usuario = usuarios.find(u => u.email === email && u.senha === dados.senha);
    if (!usuario) {
      throw new Error('Email ou senha inválidos.');
    }

    this._usuarioLogado.set(usuario);
  }

  async register(dados: { nome: string; email: string; senha: string }) {
    const novo = { nome: dados.nome.trim(), email: normalizar(dados.email), senha: dados.senha };

    if (!novo.nome) {
      throw new Error('Informe seu nome.');
    }

    try {
      await firstValueFrom(this.http.post<Usuario>(this.url, novo));
    } catch (e) {
      // 409 = e-mail já cadastrado (no banco o e-mail é único)
      if (e instanceof HttpErrorResponse && e.status === 409) {
        throw new Error('Já existe um usuário com este email.');
      }
      throw e;
    }

    await this.login(novo); // depois de cadastrar, já entra logado
  }

  // Busca o usuário pelo e-mail (rota /usuarios/email/{email}) e troca a senha com PUT.
  async redefinirSenha(email: string, novaSenha: string) {
    try {
      const usuario = await firstValueFrom(
        this.http.get<Usuario>(`${this.url}/email/${encodeURIComponent(normalizar(email))}`)
      );
      await this.atualizarUsuario({ ...usuario, senha: novaSenha });
    } catch (e) {
      if (e instanceof HttpErrorResponse && e.status === 404) {
        throw new Error('Email não encontrado.');
      }
      throw e;
    }
  }

  // ---------- CRUD de usuários (tela de administração) ----------

  listarUsuarios() {
    return firstValueFrom(this.http.get<Usuario[]>(this.url));
  }

  atualizarUsuario(usuario: Usuario) {
    return firstValueFrom(this.http.put<Usuario>(`${this.url}/${usuario.id}`, usuario));
  }

  excluirUsuario(id: number) {
    return firstValueFrom(this.http.delete(`${this.url}/${id}`));
  }
}
