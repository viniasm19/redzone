import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Usuario } from '../../models/usuario.model';

// Tela de administração de usuários (rota protegida pelo adminGuard).
@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss'
})
export class UsuariosComponent implements OnInit {
  private auth = inject(AuthService);

  // Validação do e-mail digitado: texto@dominio.ext, sem espaços.
  private static readonly EMAIL_VALIDO = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

  usuarios: Usuario[] = [];
  usuarioEditando: Usuario | null = null;
  erro = '';

  ngOnInit() {
    this.carregar();
  }

  // Padroniza o e-mail digitado: "  Lucas@Email.com " vira "lucas@email.com".
  private normalizarEmail(email: string): string {
    return email.trim().toLowerCase();
  }

  async carregar() {
    this.usuarios = await this.auth.listarUsuarios();
  }

  // Edita uma CÓPIA: assim, se o admin desistir, a lista não fica alterada.
  // A API não devolve a senha, então o campo começa vazio: o admin digita a senha (nova ou a atual).
  editar(usuario: Usuario) {
    this.usuarioEditando = { ...usuario, senha: '' };
    this.erro = '';
  }

  cancelar() {
    this.usuarioEditando = null;
    this.erro = '';
  }

  async salvarEdicao() {
    if (!this.usuarioEditando) return;
    this.erro = '';

    const nome = this.usuarioEditando.nome.trim();
    const email = this.normalizarEmail(this.usuarioEditando.email);
    const senha = this.usuarioEditando.senha ?? '';

    if (!nome) {
      this.erro = 'Informe o nome.';
      return;
    }
    if (!UsuariosComponent.EMAIL_VALIDO.test(email)) {
      this.erro = 'Digite um email válido.';
      return;
    }
    if (senha.length < 4) {
      this.erro = 'Informe a senha (mínimo 4 caracteres).';
      return;
    }

    try {
      await this.auth.atualizarUsuario({ ...this.usuarioEditando, nome, email, senha });
      await this.carregar();
      this.usuarioEditando = null;
    } catch (e) {
      // 409 = esse e-mail já pertence a outro usuário (no banco o e-mail é único)
      this.erro = e instanceof HttpErrorResponse && e.status === 409
        ? 'Já existe um usuário com este email.'
        : 'Não foi possível salvar o usuário.';
    }
  }

  async excluir(id: number) {
    this.erro = '';
    try {
      await this.auth.excluirUsuario(id);
      await this.carregar();
    } catch (e) {
      // 409 = usuário que já tem pedidos não pode ser excluído
      this.erro = e instanceof HttpErrorResponse && e.status === 409
        ? 'Este usuário já tem pedidos e não pode ser excluído.'
        : 'Não foi possível excluir o usuário.';
    }
  }
}
