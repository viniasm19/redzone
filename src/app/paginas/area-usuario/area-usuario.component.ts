import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AutenticacaoService } from '../../servicos/autenticacao.service';

@Component({
  selector: 'app-area-usuario',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './area-usuario.component.html',
  styleUrl: './area-usuario.component.scss'
})
// Minha Área: nome, e-mail e botões.
export class AreaUsuarioComponent {

  constructor(
    public auth: AutenticacaoService,
    private router: Router
  ) {}

  sair() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
