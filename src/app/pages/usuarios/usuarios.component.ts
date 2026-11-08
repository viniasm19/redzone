import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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

  usuarios: Usuario[] = [];
  usuarioEditando: Usuario | null = null;

  ngOnInit() {
    this.carregar();
  }

  async carregar() {
    this.usuarios = await this.auth.listarUsuarios();
  }

  // Edita uma CÓPIA: assim, se o admin desistir, a lista não fica alterada.
  editar(usuario: Usuario) {
    this.usuarioEditando = { ...usuario };
  }

  async salvarEdicao() {
    if (!this.usuarioEditando) return;

    await this.auth.atualizarUsuario(this.usuarioEditando);
    await this.carregar();
    this.usuarioEditando = null;
  }

  async excluir(id: number) {
    await this.auth.excluirUsuario(id);
    await this.carregar();
  }
}
