import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
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

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  // Se a pessoa veio do checkout, guarda para onde voltar depois de entrar (só caminhos do próprio site).
  get returnUrl(): string | null {
    const url = this.route.snapshot.queryParamMap.get('returnUrl');
    return url && url.startsWith('/') && !url.startsWith('//') ? url : null;
  }

  // Validação do e-mail digitado: texto@dominio.ext, sem espaços.
  private static readonly EMAIL_VALIDO = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

  // Padroniza o e-mail digitado: "  Lucas@Email.com " vira "lucas@email.com".
  // (fica aqui, na tela onde o usuário digita; o AuthService recebe o e-mail já pronto)
  private normalizarEmail(email: string): string {
    return email.trim().toLowerCase();
  }

  // Mensagem de erro do campo e-mail ('' = e-mail válido). A tela usa para avisar o usuário.
  get erroEmail(): string {
    const email = this.normalizarEmail(this.email);
    if (!email) {
      return 'Informe seu email.';
    }
    return ForgotPasswordComponent.EMAIL_VALIDO.test(email) ? '' : 'Digite um email válido.';
  }

  async resetar(form: NgForm) {
    this.erro = '';
    this.sucesso = '';

    if (form.invalid || this.erroEmail) {
      return;
    }

    this.email = this.normalizarEmail(this.email);

    try {
      await this.auth.redefinirSenha(this.email, this.novaSenha);
      this.sucesso = 'Senha atualizada com sucesso!';

      setTimeout(() => this.router.navigate(['/login'], { queryParams: this.returnUrl ? { returnUrl: this.returnUrl } : {} }), 2000);

    } catch (e: any) {
      this.erro = e?.message ?? 'Erro ao resetar senha.';
    }
  }
}
