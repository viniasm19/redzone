import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ListaProdutosComponent } from '../../componentes/lista-produtos/lista-produtos.component';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule, ListaProdutosComponent,RouterModule],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.scss',
})
// Tela inicial: banner, categorias e produtos.
export class InicioComponent {
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

