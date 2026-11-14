import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Produto, emPromocao } from '../../models/produto.model';
import { ESTOQUE_MINIMO } from '../../services/stock.service';

@Component({
  selector: 'app-product-item',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './product-item.component.html',
  styleUrls: ['./product-item.component.scss']
})
// Card de um produto.
export class ProductItemComponent {
  // Produto que o card mostra (vem de fora).
  @Input() produto!: Produto;
  // Avisa quando clicam em Adicionar.
  @Output() addToCart = new EventEmitter<Produto>();
  estoqueMinimo = ESTOQUE_MINIMO;

  get emPromocao() {
    return emPromocao(this.produto);
  }

  adicionar() {
    this.addToCart.emit(this.produto);
  }
}
