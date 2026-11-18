import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Produto } from '../../modelos/produto.model';
import { ESTOQUE_MINIMO } from '../../servicos/estoque.service';

@Component({
  selector: 'app-item-produto',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './item-produto.component.html',
  styleUrls: ['./item-produto.component.scss']
})
// Card de um produto.
export class ItemProdutoComponent {
  // Produto que o card mostra (vem de fora).
  @Input() produto!: Produto;
  // Avisa quando clicam em Adicionar.
  @Output() aoAdicionar = new EventEmitter<Produto>();
  estoqueMinimo = ESTOQUE_MINIMO;


  adicionar() {
    this.aoAdicionar.emit(this.produto);
  }
}
