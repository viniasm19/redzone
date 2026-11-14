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
// Tela de login.
export class LoginComponent {
  email = '';
  senha = '';
  erro = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  private static readonly EMAIL_VALIDO = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

  private normalizarEmail(email: string): string {
    return email.trim().toLowerCase();
  }

  get erroEmail(): string {
    const email = this.normalizarEmail(this.email);
    if (!email) {
      return 'Informe seu email.';
    }
    return LoginComponent.EMAIL_VALIDO.test(email) ? '' : 'Digite um email válido.';
  }

  get returnUrl(): string | null {
    const url = this.route.snapshot.queryParamMap.get('returnUrl');
    return url && url.startsWith('/') && !url.startsWith('//') ? url : null;
  }

  // Faz o login e volta para onde estava.
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
