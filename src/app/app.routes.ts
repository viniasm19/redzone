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

// Mapa de rotas: endereço da barra do navegador -> tela.
// canActivate = "porteiro" da rota:
//   authGuard  -> só quem está logado (checkout, pedido, área do usuário)
//   adminGuard -> só o administrador (lista de usuários e estoque)
// Cada linha: { path: 'endereço', component: TelaQueAparece, canActivate: [porteiro] }.
// O endereço vazio ('') é a tela inicial. pathMatch: 'full' = só quando o endereço é EXATAMENTE vazio.
// /products e /promocoes continuam existindo, mas não têm mais link no menu (os produtos ficam na Home).
export const routes: Routes = [
  // Tela inicial.
  { path: '', pathMatch: 'full', component: HomeComponent },
  // Tela de login.
  { path: 'login', component: LoginComponent },
  // Tela de cadastro.
  { path: 'cadastro', component: RegisterComponent },
  // Administração de usuários (só admin).
  { path: 'usuarios', component: UsuariosComponent, canActivate: [adminGuard] },
  // Controle de estoque (só admin).
  { path: 'estoque', component: EstoqueComponent, canActivate: [adminGuard] },
  // Lista de produtos (sem link no menu).
  { path: 'products', component: ProductListComponent },
  // Página de promoções (sem link no menu).
  { path: 'promocoes', component: PromocoesComponent },
  // Carrinho.
  { path: 'cart', component: CartComponent },
  // Detalhe do produto. O :id é o número do produto.
  { path: 'product/:id', component: ProductDetailComponent },
  // Checkout (precisa estar logado).
  { path: 'order-confirm', component: OrderConfirmComponent, canActivate: [authGuard] },
  // Tela de pedido confirmado (precisa estar logado).
  { path: 'order-success', component: OrderSuccessComponent, canActivate: [authGuard] },
  // Minha Área (precisa estar logado).
  { path: 'area-usuario', component: AreaUsuarioComponent, canActivate: [authGuard] },
  // Tela de redefinir senha.
  { path: 'forgot-password', component: ForgotPasswordComponent }
];