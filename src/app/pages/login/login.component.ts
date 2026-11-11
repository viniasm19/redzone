import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

// @Component: transforma a classe numa TELA do Angular.
//   selector: o nome da tag (<app-login>); templateUrl: o HTML; styleUrl: o SCSS.
//   imports: o que o HTML usa (CommonModule = *ngIf; FormsModule = ngModel/ngForm; RouterModule = routerLink).
//   standalone: o componente não precisa de um "módulo" em volta (jeito novo do Angular).
//
// O HTML espera daqui: email e senha (ligados aos campos com [(ngModel)]), erro (mensagem da API),
// erroEmail (mensagem do campo e-mail), returnUrl (para os links) e o método entrar(f).
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  email = '';   // o que a pessoa digita no campo e-mail ([(ngModel)] mantém os dois sempre iguais)
  senha = '';   // o que a pessoa digita no campo senha
  erro = '';    // mensagem de erro que vem do login (ex.: "Email ou senha inválidos.")

  // O Angular entrega estes objetos prontos (injeção de dependência):
  //   auth = login; router = trocar de tela; route = ler o endereço atual (o ?returnUrl=)
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

  // Botão "Entrar" (ngSubmit do formulário). Recebe: o formulário, para saber se está válido.
  async entrar(form: NgForm) {
    // 1) apaga o erro da tentativa anterior
    this.erro = '';

    // 2) campos inválidos: não chama a API (os avisos aparecem embaixo de cada campo)
    if (form.invalid || this.erroEmail) {
      return;
    }

    // 3) padroniza o e-mail ("  Lucas@Email.com " -> "lucas@email.com")
    this.email = this.normalizarEmail(this.email);

    try {
      // 4) pede o login ao AuthService (que chama POST /login)
      await this.auth.login({
        email: this.email,
        senha: this.senha
      });
      // 5) deu certo: volta para a página que pediu o login (ex.: o checkout) ou para a Home
      this.router.navigateByUrl(this.returnUrl ?? '/');
    } catch (e: any) {
      // 6) deu errado: mostra a mensagem do AuthService (ou uma genérica)
      this.erro = e?.message ?? 'Falha ao entrar.';
    }
  }
}
