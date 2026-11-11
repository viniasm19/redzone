import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
// Tela de cadastro (/cadastro). O HTML espera daqui: nome, email, senha (ligados aos campos),
// erro, erroEmail, returnUrl e o método cadastrar(f). Funciona igual ao LoginComponent.
export class RegisterComponent {
  nome = '';   // campo Nome
  email = '';  // campo Email
  senha = '';  // campo Senha
  erro = '';   // mensagem de erro (ex.: e-mail já cadastrado)

  // auth = cadastro; router = trocar de tela; route = ler o ?returnUrl= do endereço
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
    return RegisterComponent.EMAIL_VALIDO.test(email) ? '' : 'Digite um email válido.';
  }

  // Mesma regra do login: só aceita caminhos do próprio site.
  get returnUrl(): string | null {
    const url = this.route.snapshot.queryParamMap.get('returnUrl');
    return url && url.startsWith('/') && !url.startsWith('//') ? url : null;
  }

  // Botão "Criar conta". Recebe: o formulário.
  async cadastrar(form: NgForm) {
    // 1) apaga o erro anterior
    this.erro = '';

    // 2) campos inválidos: não chama a API
    if (form.invalid || this.erroEmail) {
      return;
    }

    // 3) padroniza o e-mail
    this.email = this.normalizarEmail(this.email);

    try {
      // 4) cadastra (POST /usuarios) e já faz o login (o AuthService faz os dois)
      await this.auth.register({
        nome: this.nome,
        email: this.email,
        senha: this.senha
      });
      // 5) volta para onde estava (ex.: checkout) ou para a Home
      this.router.navigateByUrl(this.returnUrl ?? '/');
    } catch (e: any) {
      // 6) mostra o erro (ex.: "Já existe um usuário com este email.")
      this.erro = e?.message ?? 'Falha ao cadastrar.';
    }
  }
}
