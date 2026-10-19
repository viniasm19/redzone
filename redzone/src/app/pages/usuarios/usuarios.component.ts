import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  AuthService,
  Usuario
} from '../../services/auth.service';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss'
})
export class UsuariosComponent {

  usuarios: Usuario[] = [];

  usuarioEditando: Usuario | null = null;

  constructor(
    private auth: AuthService
  ) {}

  async ngOnInit() {

    await this.carregarUsuarios();

  }

  async carregarUsuarios() {

    this.usuarios =
      await this.auth.listarUsuarios();

  }

  editar(usuario: Usuario) {

    this.usuarioEditando = {
      ...usuario
    };

  }

  async salvarEdicao() {

    if (!this.usuarioEditando) return;

    await this.auth.atualizarUsuario(
      this.usuarioEditando
    );

    await this.carregarUsuarios();

    this.usuarioEditando = null;

  }

  async excluir(id: number) {

    await this.auth.excluirUsuario(id);

    await this.carregarUsuarios();

  }

}