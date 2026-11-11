import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Produto, emPromocao } from '../../models/produto.model';
import { ESTOQUE_MINIMO } from '../../services/stock.service';

// Card de um produto. Não sabe nada do carrinho: só avisa quem o usa ("addToCart").
@Component({
  selector: 'app-product-item',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './product-item.component.html',
  styleUrls: ['./product-item.component.scss']
})
export class ProductItemComponent {
  @Input() produto!: Produto;
  @Output() addToCart = new EventEmitter<Produto>();
  estoqueMinimo = ESTOQUE_MINIMO;

  get emPromocao() {
    return emPromocao(this.produto);
  }

  adicionar() {
    this.addToCart.emit(this.produto);
  }
}
