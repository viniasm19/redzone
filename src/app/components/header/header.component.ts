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
  public cartService = inject(CartService); // public: o HTML lê cartService.quantidadeTotal()

  usuario = this.auth.usuarioLogado;                       // signal com quem está logado (ou null)
  // computed = valor calculado a partir de signals.
  isLogado = computed(() => this.auth.isAuthenticated());
  isAdmin = computed(() => this.auth.isAdmin());           // (o menu não usa mais; mantido para quem precisar)

  // Menu do celular aberto ou fechado.
  menuAberto = signal(false);

  // Botão ☰: abre se estava fechado, fecha se estava aberto
  alternarMenu() {
    this.menuAberto.update(aberto => !aberto);
  }

  // Ao clicar num link.
  fecharMenu() {
    this.menuAberto.set(false);
  }

  // Link "Sair": 1) fecha o menu do celular; 2) desloga; 3) volta para a Home
  sair() {
    this.fecharMenu();
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
