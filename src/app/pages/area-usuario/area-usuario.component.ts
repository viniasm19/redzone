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
// Tela "Minha Área" (/area-usuario). O HTML espera daqui: auth (nome, e-mail e se é admin),
// pedidos/carregando/erro (histórico), textoStatus e textoForma (traduções) e o método sair().
export class AreaUsuarioComponent implements OnInit {

  // Histórico de pedidos do usuário logado (do mais novo para o mais antigo, a API já ordena)
  pedidos: Pedido[] = [];
  carregando = true;
  erro = '';

  // auth é public porque o HTML lê auth.usuarioLogado() e auth.isAdmin()
  constructor(
    public auth: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {}

  // Roda quando a tela abre: carrega os pedidos do usuário logado
  async ngOnInit() {
    // 1) pega quem está logado
    const usuario = this.auth.usuarioLogado();
    if (!usuario) {
      return; // o authGuard já impede, mas por garantia
    }

    try {
      // 2) GET /pedidos/usuario/{id}
      this.pedidos = await this.orderService.listarPorUsuario(usuario.id);
    } catch {
      // 3) se a API falhar, mostra o aviso no lugar da lista
      this.erro = 'Não foi possível carregar seus pedidos.';
    } finally {
      // 4) terminou (com ou sem erro): tira o "Carregando..."
      this.carregando = false;
    }
  }

  // Recebe: o status da API (ex.: 'EM_SEPARACAO'). Devolve: o texto da tela ("Em separação").
  textoStatus(status: StatusPedido) {
    return STATUS_PEDIDO_TEXTO[status] ?? status;
  }

  // Tabela de tradução da forma de pagamento ('PIX' -> "Pix"), usada direto no HTML
  textoForma = FORMA_PAGAMENTO_TEXTO;

  // Botão "Sair": desloga e volta para a Home
  sair() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
