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
// Tela inicial: banner, categorias e produtos.
export class HomeComponent {
  categorias = ['Todos', 'Áudio', 'Móveis', 'Hardware', 'Acessórios'];
  categoriaSelecionada = 'Todos';

  // Escolhe a categoria do filtro.
  selecionarCategoria(c: string) {
    this.categoriaSelecionada = c;
  }

  // Rola até a vitrine.
  irParaProdutos() {
    document.getElementById('produtos')?.scrollIntoView({ behavior: 'smooth' });
  }
}

