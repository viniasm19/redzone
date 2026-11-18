import { Routes } from '@angular/router';
import { InicioComponent } from './paginas/inicio/inicio.component';
import { LoginComponent } from './paginas/login/login.component';
import { CadastroComponent } from './paginas/cadastro/cadastro.component';
import { UsuariosComponent } from './paginas/usuarios/usuarios.component';
import { ListaProdutosComponent } from './componentes/lista-produtos/lista-produtos.component';
import { CarrinhoComponent } from './paginas/carrinho/carrinho.component';
import { DetalheProdutoComponent } from './paginas/detalhe-produto/detalhe-produto.component';
import { ConfirmarPedidoComponent } from './paginas/confirmar-pedido/confirmar-pedido.component';
import { PedidoConfirmadoComponent } from './paginas/pedido-confirmado/pedido-confirmado.component';
import { EsqueciSenhaComponent } from './paginas/esqueci-senha/esqueci-senha.component';
import { AreaUsuarioComponent } from './paginas/area-usuario/area-usuario.component';
import { EstoqueComponent } from './paginas/estoque/estoque.component';
import { autenticacaoGuard } from './guardas/autenticacao.guard';
import { adminGuard } from './guardas/admin.guard';

// Mapa de telas: endereço -> componente.
export const routes: Routes = [
  // Tela inicial.
  { path: '', pathMatch: 'full', component: InicioComponent },
  { path: 'login', component: LoginComponent },
  { path: 'cadastro', component: CadastroComponent },
  // Usuários (só admin).
  { path: 'usuarios', component: UsuariosComponent, canActivate: [adminGuard] },
  // Estoque (só admin).
  { path: 'estoque', component: EstoqueComponent, canActivate: [adminGuard] },
  { path: 'products', component: ListaProdutosComponent },
  // Carrinho.
  { path: 'cart', component: CarrinhoComponent },
  { path: 'product/:id', component: DetalheProdutoComponent },
  // Checkout (precisa estar logado).
  { path: 'order-confirm', component: ConfirmarPedidoComponent, canActivate: [autenticacaoGuard] },
  { path: 'order-success', component: PedidoConfirmadoComponent, canActivate: [autenticacaoGuard] },
  { path: 'area-usuario', component: AreaUsuarioComponent, canActivate: [autenticacaoGuard] },
  { path: 'forgot-password', component: EsqueciSenhaComponent }
];