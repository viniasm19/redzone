import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { UsuariosComponent } from './pages/usuarios/usuarios.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { CartComponent } from './pages/cart/cart.component';
import { ProductDetailComponent } from './pages/product-detail/product-detail.component';
import { OrderConfirmComponent } from './pages/order-confirm/order-confirm.component';
import { OrderSuccessComponent } from './pages/order-success/order-success.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { AreaUsuarioComponent } from './pages/area-usuario/area-usuario.component';
import { EstoqueComponent } from './pages/estoque/estoque.component';
import { PromocoesComponent } from './pages/promocoes/promocoes.component';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

// Mapa de telas: endereço -> componente.
export const routes: Routes = [
  // Tela inicial.
  { path: '', pathMatch: 'full', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'cadastro', component: RegisterComponent },
  // Usuários (só admin).
  { path: 'usuarios', component: UsuariosComponent, canActivate: [adminGuard] },
  // Estoque (só admin).
  { path: 'estoque', component: EstoqueComponent, canActivate: [adminGuard] },
  { path: 'products', component: ProductListComponent },
  { path: 'promocoes', component: PromocoesComponent },
  // Carrinho.
  { path: 'cart', component: CartComponent },
  { path: 'product/:id', component: ProductDetailComponent },
  // Checkout (precisa estar logado).
  { path: 'order-confirm', component: OrderConfirmComponent, canActivate: [authGuard] },
  { path: 'order-success', component: OrderSuccessComponent, canActivate: [authGuard] },
  { path: 'area-usuario', component: AreaUsuarioComponent, canActivate: [authGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent }
];