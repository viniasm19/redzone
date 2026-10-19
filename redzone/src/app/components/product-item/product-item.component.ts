import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Produto } from '../../services/cart.service';

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

  adicionar() {
    this.addToCart.emit(this.produto);
  }
}