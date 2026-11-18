import { Component } from '@angular/core';
import { LOJA } from '../../configuracao/loja';

@Component({
  selector: 'app-rodape',
  standalone: true,
  templateUrl: './rodape.component.html',
  styleUrl: './rodape.component.scss'
})
// Rodapé com dados da loja.
export class RodapeComponent {
  loja = LOJA;
  ano = new Date().getFullYear();
}
