import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductListComponent } from '../../components/product-list/product-list.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ProductListComponent,RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
// Tela inicial ('/'). Mostra a apresentação.
// O HTML espera daqui: categorias (os botões).
export class HomeComponent {
  categorias = ['Todos', 'Áudio', 'Móveis', 'Hardware', 'Acessórios']; // botões da barra lateral
  categoriaSelecionada = 'Todos';                                        // a categoria escolhida agora

  // Clique numa categoria: guarda a escolha.
  selecionarCategoria(c: string) {
    this.categoriaSelecionada = c;
  }

  // Botão "Ver Produtos" do topo.
  irParaProdutos() {
    document.getElementById('produtos')?.scrollIntoView({ behavior: 'smooth' });
  }
}

