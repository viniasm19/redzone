import { Injectable, signal, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { API_URL } from '../config/api';

export interface Usuario {
  id?: number;
  nome: string;
  email: string;
  senha: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly URL = `${API_URL}/usuarios`;

  private http = inject(HttpClient);

  private _usuarioLogado = signal<Usuario | null>(null);

  usuarioLogado = this._usuarioLogado.asReadonly();

  private async obterUsuarios(): Promise<Usuario[]> {

    return await firstValueFrom(
      this.http.get<Usuario[]>(this.URL)
    );
  }

  isAuthenticated() {
    return !!this._usuarioLogado();
  }

  currentUser(): Usuario | null {
    return this._usuarioLogado();
  }


  register(input: {
    nome: string;
    email: string;
    senha: string;
  }) {

    return (async () => {

      const nome = input.nome.trim();
      const email = input.email;
      const senha = input.senha;

      if (!nome) {
        throw new Error('Informe seu nome.');
      }

      if (!email) {
        throw new Error('Informe seu email.');
      }

      if (!senha || senha.length < 4) {
        throw new Error(
          'A senha deve ter pelo menos 4 caracteres.'
        );
      }

      const usuarios = await this.obterUsuarios();

      const exists = usuarios.some(
        u => u.email === email
      );

      if (exists) {
        throw new Error(
          'Já existe um usuário com este email.'
        );
      }

      const novoId =
        (usuarios.reduce((max, u) => (u.id && u.id > max ? u.id : max), 0) || 0) + 1;

      const payload: Usuario = {
        id: novoId,
        nome,
        email,
        senha
      };

      let criado: Usuario;

      try {
        criado = await firstValueFrom(
          this.http.post<Usuario>(
            this.URL,
            payload
          )
        );
      } catch (e) {
        if (e instanceof HttpErrorResponse && e.status === 409) {
          throw new Error(
            'Já existe um usuário com este email.'
          );
        }
        throw e;
      }


      await this.login({
        email,
        senha
      });

      return criado;

    })();
  }


  login(input: {
    email: string;
    senha: string;
  }) {

    return (async () => {

      const email = input.email;
      const senha = input.senha;

      const usuarios = await this.obterUsuarios();

      const user = usuarios.find(
        u =>
          u.email === email &&
          u.senha === senha
      );

      if (!user) {
        throw new Error(
          'Email ou senha inválidos.'
        );
      }

      this._usuarioLogado.set(user);

      return user;

    })();
  }

  async buscarPorEmail(email: string): Promise<Usuario | null> {

    try {

      return await firstValueFrom(
        this.http.get<Usuario>(
          `${this.URL}/email/${encodeURIComponent(email)}`
        )
      );

    } catch (e) {

      if (e instanceof HttpErrorResponse && e.status === 404) {
        return null;
      }

      throw e;
    }
  }


  async redefinirSenha(email: string, novaSenha: string): Promise<void> {

    const usuario = await this.buscarPorEmail(email);

    if (!usuario) {
      throw new Error('Email não encontrado.');
    }

    await this.atualizarUsuario({ ...usuario, senha: novaSenha });
  }


  async listarUsuarios(): Promise<Usuario[]> {

    return await firstValueFrom(
      this.http.get<Usuario[]>(this.URL)
    );
  }


  async excluirUsuario(id: number) {

    return await firstValueFrom(
      this.http.delete(
        `${this.URL}/${id}`
      )
    );
  }



  async atualizarUsuario(usuario: Usuario) {

    if (!usuario.id) {
      throw new Error('Usuário sem ID.');
    }

    return await firstValueFrom(

      this.http.put<Usuario>(
        `${this.URL}/${usuario.id}`,
        usuario
      )

    );
  }


  logout() {
    this._usuarioLogado.set(null);
  }

  isAdmin(): boolean {
    const user = this._usuarioLogado();
    return user?.email === 'admin@redzone.com';
  }

}
