import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-area-usuario',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './area-usuario.component.html',
  styleUrl: './area-usuario.component.scss'
})
export class AreaUsuarioComponent {

  constructor(
    public auth: AuthService,
    private router: Router
  ) {}

  sair() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
