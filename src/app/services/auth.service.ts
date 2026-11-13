import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';
import { Usuario, UsuarioLogado } from '../models/usuario.model';

// Cuida de TUDO que é sobre conta: login, cadastro, logout.
// @Injectable({ providedIn: 'root' }).
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);       // faz as chamadas para a API
  private url = `${API_URL}/usuarios`;     // http://localhost:8080/usuarios

  // Nome da "gaveta" do sessionStorage onde o usuário logado fica guardado
  private static readonly CHAVE = 'redzone.usuario';

  // Quem está logado. Fica guardado no sessionStorage.
  // mas fechar a aba sim. A senha nunca é guardada (a API só devolve id.
  private _usuarioLogado = signal<UsuarioLogado | null>(this.lerSalvo());
  usuarioLogado = this._usuarioLogado.asReadonly();

  // Lê o usuário guardado no sessionStorage (roda uma vez, quando o app abre).
  // Devolve: o usuário.
  private lerSalvo(): UsuarioLogado | null {
    try {
      // 1) pega o texto guardado (o storage só guarda texto)
      const texto = sessionStorage.getItem(AuthService.CHAVE);
      // 2) se tem texto, transforma de volta em objeto (JSON.parse); se não tem, null
      return texto ? (JSON.parse(texto) as UsuarioLogado) : null;
    } catch {
      return null;
    }
  }

  // Troca quem está logado. Recebe: o usuário (login) ou null (logout).
  private salvar(usuario: UsuarioLogado | null) {
    // 1) atualiza o signal: o cabeçalho e as telas que mostram o nome mudam na hora
    this._usuarioLogado.set(usuario);
    try {
      // 2) guarda (ou apaga) a cópia no sessionStorage, para o F5 não deslogar
      if (usuario) {
        sessionStorage.setItem(AuthService.CHAVE, JSON.stringify(usuario));
      } else {
        sessionStorage.removeItem(AuthService.CHAVE);
      }
    } catch {
      // sem storage (modo privado, por exemplo): continua só em memória
    }
  }

  // true se tem alguém logado (usado pelo authGuard e pelo cabeçalho)
  isAuthenticated() {
    return this._usuarioLogado() !== null;
  }

  // O administrador é quem tem este e-mail (regra simples.
  isAdmin() {
    return this._usuarioLogado()?.email === 'admin@redzone.com';
  }

  // Sair: esquece o usuário (no signal e no sessionStorage)
  logout() {
    this.salvar(null);
  }

  // ---------- Login e cadastro ----------

  // POST /login: quem confere e-mail e senha é o back-end. A API devolve só id.
  // (a senha nunca volta para o navegador).
  async login(dados: { email: string; senha: string }) {
    try {
      // 1) POST /login e espera a resposta (firstValueFrom transforma a resposta numa.
      const usuario = await firstValueFrom(
        this.http.post<UsuarioLogado>(`${API_URL}/login`, {
          email: dados.email,
          senha: dados.senha
        })
      );
      // 2) deu certo: guarda quem entrou
      this.salvar(usuario);
    } catch (e) {
      // 3) deu errado: traduz o erro HTTP numa mensagem que a tela de login mostra
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
      throw e; // outro erro qualquer: repassa para quem chamou
    }
  }

  // Cadastro. Recebe: nome, e-mail (já normalizado pela tela) e senha.
  async register(dados: { nome: string; email: string; senha: string }) {
    // 1) monta o usuário novo, tirando espaços das pontas do nome
    const novo = { nome: dados.nome.trim(), email: dados.email, senha: dados.senha };

    // 2) nome só com espaços não vale
    if (!novo.nome) {
      throw new Error('Informe seu nome.');
    }

    // 3) POST /usuarios para criar a conta
    try {
      await firstValueFrom(this.http.post<Usuario>(this.url, novo));
    } catch (e) {
      // 409 = e-mail já cadastrado (no banco o e-mail é único)
      if (e instanceof HttpErrorResponse && e.status === 409) {
        throw new Error('Já existe um usuário com este email.');
      }
      throw e;
    }

    // 4) conta criada: faz o login com os mesmos dados
    await this.login(novo); // depois de cadastrar, já entra logado
  }

  // Reset de senha SEM listar todos os usuários:
  // 1) GET /usuarios/email/{email} -> findByEmail no back-end
  async redefinirSenha(email: string, novaSenha: string) {
    try {
      // 1) busca o usuário pelo e-mail (a resposta vem sem a senha)
      const usuario = await firstValueFrom(
        this.http.get<Usuario>(`${this.url}/email/${encodeURIComponent(email)}`)
      );
      // 2) manda o mesmo usuário de volta, agora com a senha nova.
      // { ...usuario, senha.
      await this.atualizarUsuario({ ...usuario, senha: novaSenha });
    } catch (e) {
      if (e instanceof HttpErrorResponse && e.status === 404) {
        throw new Error('Email não encontrado.');
      }
      throw e;
    }
  }

  // ---------- CRUD de usuários (tela de administração) ----------

  // GET /usuarios: todos os usuários (a API não devolve as senhas)
  listarUsuarios() {
    return firstValueFrom(this.http.get<Usuario[]>(this.url));
  }

  // PUT /usuarios/{id}: altera um usuário (usado pelo admin e pelo reset de senha)
  atualizarUsuario(usuario: Usuario) {
    return firstValueFrom(this.http.put<Usuario>(`${this.url}/${usuario.id}`, usuario));
  }

  // DELETE /usuarios/{id} (a API responde 409 se o usuário já tiver pedidos)
  excluirUsuario(id: number) {
    return firstValueFrom(this.http.delete(`${this.url}/${id}`));
  }
}
