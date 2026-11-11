import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LOJA } from '../../config/loja';

// Rodapé que aparece em todas as telas (fica no app.component.html, depois do router-outlet).
// Os dados (endereço, telefone, redes...) vêm do config/loja.ts.
@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {
  loja = LOJA;
  ano = new Date().getFullYear(); // o ano do copyright se atualiza sozinho
}
