import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  email = '';
  senha = '';
  erro = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

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
    return LoginComponent.EMAIL_VALIDO.test(email) ? '' : 'Digite um email válido.';
  }

  // Para onde voltar depois do login (ex.: o checkout). Só aceita caminhos do próprio site:
  // bloqueia links externos como "//site-falso.com" (redirecionamento malicioso).
  get returnUrl(): string | null {
    const url = this.route.snapshot.queryParamMap.get('returnUrl');
    return url && url.startsWith('/') && !url.startsWith('//') ? url : null;
  }

  async entrar(form: NgForm) {
    this.erro = '';

    if (form.invalid || this.erroEmail) {
      return;
    }

    this.email = this.normalizarEmail(this.email);

    try {
      await this.auth.login({
        email: this.email,
        senha: this.senha
      });
      this.router.navigateByUrl(this.returnUrl ?? '/');
    } catch (e: any) {
      this.erro = e?.message ?? 'Falha ao entrar.';
    }
  }
}
