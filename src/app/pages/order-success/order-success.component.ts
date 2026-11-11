import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './order-success.component.html',
  styleUrl: './order-success.component.scss'
})
// Tela "Pedido Confirmado!" (/order-success).
// O HTML espera daqui: auth (para mostrar o nome de quem comprou) e os métodos dos dois botões.
export class OrderSuccessComponent {

  // "public auth": public para o HTML conseguir ler auth.usuarioLogado(); router é private (só a classe usa)
  constructor(
    public auth: AuthService,
    private router: Router
  ) {}

  // Botão "Continuar Comprando": volta para a Home
  continuar() {
    this.router.navigate(['/']);
  }

  // Botão "Minha Área"
  irParaAreaUsuario() {
    this.router.navigate(['/area-usuario']);
  }
}
