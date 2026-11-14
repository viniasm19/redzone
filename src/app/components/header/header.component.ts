import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
// Menu do topo (muda se está logado).
export class HeaderComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  public cartService = inject(CartService);

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
