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
// Cabeçalho: mostra Login/Cadastro ou "Olá, nome" conforme quem está logado.
// Em telas pequenas, os links ficam escondidos atrás do botão ☰ (menu recolhível).
export class HeaderComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  public cartService = inject(CartService);

  usuario = this.auth.usuarioLogado;
  isLogado = computed(() => this.auth.isAuthenticated());
  isAdmin = computed(() => this.auth.isAdmin());

  // Menu do celular aberto ou fechado. Só tem efeito visual em telas pequenas (o CSS decide).
  menuAberto = signal(false);

  alternarMenu() {
    this.menuAberto.update(aberto => !aberto);
  }

  // Ao clicar num link, o menu do celular se fecha (senão ele ficaria aberto por cima da página nova)
  fecharMenu() {
    this.menuAberto.set(false);
  }

  sair() {
    this.fecharMenu();
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
