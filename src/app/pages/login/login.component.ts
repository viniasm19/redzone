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

  get returnUrl(): string | null {
    const url = this.route.snapshot.queryParamMap.get('returnUrl');
    return url && url.startsWith('/') && !url.startsWith('//') ? url : null;
  }

  private normalizarEmail(email: string) {
    return email.trim().toLowerCase();
  }

  async entrar(form: NgForm) {
    this.erro = '';

    if (form.invalid) {
      return;
    }

    try {
      await this.auth.login({
        email: this.normalizarEmail(this.email),
        senha: this.senha
      });
      this.router.navigateByUrl(this.returnUrl ?? '/');
    } catch (e: any) {
      this.erro = e?.message ?? 'Falha ao entrar.';
    }
  }
}
