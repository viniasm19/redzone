import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CarrinhoService } from '../../servicos/carrinho.service';
import { AutenticacaoService } from '../../servicos/autenticacao.service';

@Component({
  selector: 'app-cabecalho',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cabecalho.component.html',
  styleUrls: ['./cabecalho.component.scss'],
})
// Menu do topo (muda se está logado).
export class CabecalhoComponent {
  private auth = inject(AutenticacaoService);
  private router = inject(Router);
  public carrinhoService = inject(CarrinhoService);

  usuario = this.auth.usuarioLogado;
  isLogado = computed(() => this.auth.isAuthenticated());

  // Menu do celular aberto ou fechado.
  menuAberto = signal(false);

  alternarMenu() {
    this.menuAberto.update(aberto => !aberto);
  }

  fecharMenu() {
    this.menuAberto.set(false);
  }

  sair() {
    this.fecharMenu();
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
