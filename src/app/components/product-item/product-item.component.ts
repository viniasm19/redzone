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
// Quem usa: <app-product-item [produto]="p" (addToCart)="fazAlgo($event)">.
// O HTML espera daqui: produto, estoqueMinimo, emPromocao e o método adicionar().
export class ProductItemComponent {
  // @Input: valor que vem DE FORA, de quem usa o card (o produto a mostrar). O "!" diz ao TypeScript
  // "confie, esse valor vai chegar" (ele chega pelo [produto]="...").
  @Input() produto!: Produto;
  // @Output: evento que o card "dispara" para FORA. Quem usa o card escuta com (addToCart)="...".
  @Output() addToCart = new EventEmitter<Produto>();
  estoqueMinimo = ESTOQUE_MINIMO; // até quantas unidades mostrar "Últimas X unidades"

  // true se o produto está em promoção (o HTML mostra o preço antigo riscado)
  get emPromocao() {
    return emPromocao(this.produto);
  }

  // Botão "Adicionar ao Carrinho": avisa quem usa o card, mandando o produto junto
  adicionar() {
    this.addToCart.emit(this.produto);
  }
}
