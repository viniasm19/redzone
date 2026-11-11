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
// Tela inicial ('/'). Mostra a apresentação, as categorias e a vitrine (<app-product-list>).
// O HTML espera daqui: categorias (os botões), categoriaSelecionada (o botão ativo, que também vai para a
// vitrine filtrar) e os métodos selecionarCategoria e irParaProdutos.
export class HomeComponent {
  categorias = ['Todos', 'Áudio', 'Móveis', 'Hardware', 'Acessórios']; // botões da barra lateral
  categoriaSelecionada = 'Todos';                                        // a categoria escolhida agora

  // Clique numa categoria: guarda a escolha. O HTML repassa para a vitrine com [categoriaSelecionada].
  selecionarCategoria(c: string) {
    this.categoriaSelecionada = c;
  }

  // Botão "Ver Produtos" do topo: rola a página suavemente até a vitrine (id="produtos")
  irParaProdutos() {
    document.getElementById('produtos')?.scrollIntoView({ behavior: 'smooth' });
  }
}

