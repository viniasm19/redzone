import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './order-success.component.html',
  styleUrl: './order-success.component.scss'
})
export class OrderSuccessComponent {

  constructor(
    public auth: AuthService,
    private router: Router
  ) {}

  continuar() {
    this.router.navigate(['/']);
  }

  irParaAreaUsuario() {
    this.router.navigate(['/area-usuario']);
  }
}
