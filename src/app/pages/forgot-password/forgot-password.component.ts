import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {

  email = '';
  novaSenha = '';
  erro = '';
  sucesso = '';

  constructor(private auth: AuthService, private router: Router) {}

  private normalizarEmail(email: string) {
    return email.trim().toLowerCase();
  }

  async resetar(form: NgForm) {
    this.erro = '';
    this.sucesso = '';

    if (form.invalid) {
      return;
    }

    try {
      await this.auth.redefinirSenha(
        this.normalizarEmail(this.email),
        this.novaSenha
      );
      this.sucesso = 'Senha atualizada com sucesso!';

      setTimeout(() => this.router.navigate(['/login']), 2000);

    } catch (e: any) {
      this.erro = e?.message ?? 'Erro ao resetar senha.';
    }
  }
}
