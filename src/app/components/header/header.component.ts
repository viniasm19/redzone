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
// O HTML espera daqui: cartService (contador do carrinho), usuario (nome), isLogado (qual grupo mostrar),
// menuAberto (menu do celular) e os métodos alternarMenu, fecharMenu e sair.
export class HeaderComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  public cartService = inject(CartService); // public: o HTML lê cartService.quantidadeTotal()

  usuario = this.auth.usuarioLogado;                       // signal com quem está logado (ou null)
  // computed = valor calculado a partir de signals; recalcula sozinho quando alguém loga ou sai
  isLogado = computed(() => this.auth.isAuthenticated());
  isAdmin = computed(() => this.auth.isAdmin());           // (o menu não usa mais; mantido para quem precisar)

  // Menu do celular aberto ou fechado. Só tem efeito visual em telas pequenas (o CSS decide).
  menuAberto = signal(false);

  // Botão ☰: abre se estava fechado, fecha se estava aberto
  alternarMenu() {
    this.menuAberto.update(aberto => !aberto);
  }

  // Ao clicar num link, o menu do celular se fecha (senão ele ficaria aberto por cima da página nova)
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
