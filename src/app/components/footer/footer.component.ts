import { Component } from '@angular/core';
import { LOJA } from '../../config/loja';

// Rodapé que aparece em todas as telas (fica no app.component.html.
// Mostra só o essencial: nome da loja, endereço, telefone e e-mail.
@Component({
  selector: 'app-footer',
  standalone: true,
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {
  loja = LOJA;                     // o HTML lê loja.endereco, loja.telefone...
  ano = new Date().getFullYear();  // o ano do copyright se atualiza sozinho
}
