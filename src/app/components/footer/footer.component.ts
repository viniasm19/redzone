import { Component } from '@angular/core';
import { LOJA } from '../../config/loja';

@Component({
  selector: 'app-footer',
  standalone: true,
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
// Rodapé com dados da loja.
export class FooterComponent {
  loja = LOJA;
  ano = new Date().getFullYear();
}
