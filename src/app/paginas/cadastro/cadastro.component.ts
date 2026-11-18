import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AutenticacaoService } from '../../servicos/autenticacao.service';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.scss',
})
export class CadastroComponent {
  nome = '';
  email = '';
  senha = '';
  erro = '';

  constructor(
    private auth: AutenticacaoService,
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
    return CadastroComponent.EMAIL_VALIDO.test(email) ? '' : 'Digite um email válido.';
  }

  get returnUrl(): string | null {
    const url = this.route.snapshot.queryParamMap.get('returnUrl');
    return url && url.startsWith('/') && !url.startsWith('//') ? url : null;
  }

  async cadastrar(form: NgForm) {
    this.erro = '';

    if (form.invalid || this.erroEmail) {
      return;
    }

    this.email = this.normalizarEmail(this.email);

    try {
      await this.auth.register({
        nome: this.nome,
        email: this.email,
        senha: this.senha
      });
      this.router.navigateByUrl(this.returnUrl ?? '/');
    } catch (e: any) {
      this.erro = e?.message ?? 'Falha ao cadastrar.';
    }
  }
}
