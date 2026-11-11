import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Usuario, UsuarioLogado } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private url = `${API_URL}/usuarios`;

  private static readonly CHAVE = 'redzone.usuario';

  // Quem está logado. Fica guardado no sessionStorage: recarregar a página (F5) NÃO desloga,
  // mas fechar a aba sim. A senha nunca é guardada (a API só devolve id, nome e e-mail).
  // signal = valor que o Angular observa; quando muda, as telas que o usam atualizam sozinhas.
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
      // sem storage (modo privado, por exemplo): continua só em memória
    }
  }

  isAuthenticated() {
    return this._usuarioLogado() !== null;
  }

  // O administrador é quem tem este e-mail (regra simples, só para o trabalho acadêmico).
  isAdmin() {
    return this._usuarioLogado()?.email === 'admin@redzone.com';
  }

  logout() {
    this.salvar(null);
  }

  // ---------- Login e cadastro ----------

  // POST /login: quem confere e-mail e senha é o back-end. A API devolve só id, nome e e-mail
  // (a senha nunca volta para o navegador).
  // O e-mail já chega normalizado (trim + minúsculas): quem faz isso é a tela onde ele foi digitado.
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
        // 401 = e-mail ou senha errados
        if (e.status === 401) {
          throw new Error('Email ou senha inválidos.');
        }
        // status 0 = o navegador nem chegou na API (desligada ou porta errada)
        if (e.status === 0) {
          throw new Error('Não foi possível conectar à API. Confira se o Spring Boot está rodando na porta 8080.');
        }
      }
      throw e;
    }
  }

  async register(dados: { nome: string; email: string; senha: string }) {
    const novo = { nome: dados.nome.trim(), email: dados.email, senha: dados.senha };

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

  // Reset de senha SEM listar todos os usuários:
  //   1) GET /usuarios/email/{email} -> findByEmail no back-end
  //   2) PUT /usuarios/{id}          -> atualiza o usuário com a senha nova
  // (o e-mail também já chega normalizado pela tela)
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
