import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AutenticacaoService } from '../../servicos/autenticacao.service';

@Component({
  selector: 'app-pedido-confirmado',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './pedido-confirmado.component.html',
  styleUrl: './pedido-confirmado.component.scss'
})
// Tela de pedido confirmado.
export class PedidoConfirmadoComponent {

  constructor(
    public auth: AutenticacaoService,
    private router: Router
  ) {}

  continuar() {
    this.router.navigate(['/']);
  }

  irParaAreaUsuario() {
    this.router.navigate(['/area-usuario']);
  }
}
