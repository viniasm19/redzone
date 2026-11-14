import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Usuario, UsuarioLogado } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
// Cuida de login, cadastro, sair e senha.
export class AuthService {
  private http = inject(HttpClient);
  private url = `${API_URL}/usuarios`;

  // Chave do sessionStorage (quem está logado).
  private static readonly CHAVE = 'redzone.usuario';

  private _usuarioLogado = signal<UsuarioLogado | null>(this.lerSalvo());
  usuarioLogado = this._usuarioLogado.asReadonly();

  private lerSalvo(): UsuarioLogado | null {
    try {
      const texto = sessionStorage.getItem(AuthService.CHAVE);
      return texto ? (JSON.parse(texto) as UsuarioLogado) : null;
    } catch {
      return null;
    }
  }

  private salvar(usuario: UsuarioLogado | null) {
    this._usuarioLogado.set(usuario);
    try {
      if (usuario) {
        sessionStorage.setItem(AuthService.CHAVE, JSON.stringify(usuario));
      } else {
        sessionStorage.removeItem(AuthService.CHAVE);
      }
    } catch {
    }
  }

  // Tem alguém logado?
  isAuthenticated() {
    return this._usuarioLogado() !== null;
  }

  // É o admin (e-mail admin@redzone.com)?
  isAdmin() {
    return this._usuarioLogado()?.email === 'admin@redzone.com';
  }

  // Sai da conta.
  logout() {
    this.salvar(null);
  }


  // POST /login e guarda quem entrou.
  async login(dados: { email: string; senha: string }) {
    try {
      const usuario = await firstValueFrom(
        this.http.post<UsuarioLogado>(`${API_URL}/login`, {
          email: dados.email,
          senha: dados.senha
        })
      );
      this.salvar(usuario);
    } catch (e) {
      if (e instanceof HttpErrorResponse) {
        if (e.status === 401) {
          throw new Error('Email ou senha inválidos.');
        }
        if (e.status === 0) {
          throw new Error('Não foi possível conectar à API. Confira se o Spring Boot está rodando na porta 8080.');
        }
      }
      throw e;
    }
  }

  // Cadastra e já faz o login.
  async register(dados: { nome: string; email: string; senha: string }) {
    const novo = { nome: dados.nome.trim(), email: dados.email, senha: dados.senha };

    if (!novo.nome) {
      throw new Error('Informe seu nome.');
    }

    try {
      await firstValueFrom(this.http.post<Usuario>(this.url, novo));
    } catch (e) {
      if (e instanceof HttpErrorResponse && e.status === 409) {
        throw new Error('Já existe um usuário com este email.');
      }
      throw e;
    }

    await this.login(novo);
  }

  // Troca a senha de um usuário pelo e-mail.
  async redefinirSenha(email: string, novaSenha: string) {
    try {
      const usuario = await firstValueFrom(
        this.http.get<Usuario>(`${this.url}/email/${encodeURIComponent(email)}`)
      );
      await this.atualizarUsuario({ ...usuario, senha: novaSenha });
    } catch (e) {
      if (e instanceof HttpErrorResponse && e.status === 404) {
        throw new Error('Email não encontrado.');
      }
      throw e;
    }
  }


  // GET /usuarios (admin).
  listarUsuarios() {
    return firstValueFrom(this.http.get<Usuario[]>(this.url));
  }

  // PUT /usuarios/{id}.
  atualizarUsuario(usuario: Usuario) {
    return firstValueFrom(this.http.put<Usuario>(`${this.url}/${usuario.id}`, usuario));
  }

  // DELETE /usuarios/{id}.
  excluirUsuario(id: number) {
    return firstValueFrom(this.http.delete(`${this.url}/${id}`));
  }
}
