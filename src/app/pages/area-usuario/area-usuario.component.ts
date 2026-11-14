import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';
import { FORMA_PAGAMENTO_TEXTO, Pedido, STATUS_PEDIDO_TEXTO, StatusPedido } from '../../models/pedido.model';

@Component({
  selector: 'app-area-usuario',
  standalone: true,
  imports: [RouterModule, DatePipe],
  templateUrl: './area-usuario.component.html',
  styleUrl: './area-usuario.component.scss'
})
// Minha Área: dados e pedidos.
export class AreaUsuarioComponent implements OnInit {

  pedidos: Pedido[] = [];
  carregando = true;
  erro = '';

  constructor(
    public auth: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {}

  async ngOnInit() {
    const usuario = this.auth.usuarioLogado();
    if (!usuario) {
      return;
    }

    try {
      this.pedidos = await this.orderService.listarPorUsuario(usuario.id);
    } catch {
      this.erro = 'Não foi possível carregar seus pedidos.';
    } finally {
      this.carregando = false;
    }
  }

  // Status em texto (ex.: 'Aprovado').
  textoStatus(status: StatusPedido) {
    return STATUS_PEDIDO_TEXTO[status] ?? status;
  }

  textoForma = FORMA_PAGAMENTO_TEXTO;

  sair() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
