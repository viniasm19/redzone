import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProductItemComponent } from '../product-item/product-item.component';
import { Produto } from '../../models/produto.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

// Formulário de produto (o mesmo para cadastrar e editar). Não tem id: o id vem de editandoId.
// O estoque só é usado no CADASTRO (estoque inicial). Na edição, quem mexe no estoque é a tela de Estoque.
const FORM_VAZIO = { nome: '', preco: 0, imagem: '', categoria: '', estoque: 0 };

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductItemComponent, FormsModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private auth = inject(AuthService);

  todos: Produto[] = [];    // tudo que veio da API
  produtos: Produto[] = []; // o que aparece na tela (filtrado pela categoria)
  categoria = 'Todos';
  categorias = ['Áudio', 'Móveis', 'Hardware', 'Acessórios'];

  novoProduto: Omit<Produto, 'id'> = { ...FORM_VAZIO };
  editandoId: number | null = null; // null = cadastrando; número = editando esse produto
  erro = '';

  // A Home manda a categoria escolhida na barra lateral
  @Input() set categoriaSelecionada(cat: string) {
    this.categoria = cat || 'Todos';
    this.filtrar();
  }

  ngOnInit() {
    this.loadProdutos();
  }

  // Só o administrador vê os botões de editar, excluir e o formulário
  isAdmin() {
    return this.auth.isAdmin();
  }

  loadProdutos() {
    this.productService.getProdutos().subscribe(dados => {
      this.todos = dados;
      this.filtrar();
    });
  }

  private filtrar() {
    this.produtos =
      this.categoria === 'Todos' ? this.todos : this.todos.filter(p => p.categoria === this.categoria);
  }

  trackById(_: number, item: Produto) {
    return item.id;
  }

  // Botão do formulário: editando -> PUT (atualiza); senão -> POST (cria)
  addProduto() {
    this.erro = '';
    const p = this.novoProduto;

    if (!p.nome.trim() || !(p.preco > 0) || !p.categoria) {
      this.erro = 'Preencha nome, preço (maior que zero) e categoria.';
      return;
    }

    if (!this.editandoId && !(Number.isInteger(p.estoque) && p.estoque >= 0)) {
      this.erro = 'O estoque inicial deve ser um número inteiro, zero ou maior.';
      return;
    }

    const requisicao = this.editandoId
      ? this.productService.updateProduto({ ...p, id: this.editandoId })
      : this.productService.addProduto(p);

    requisicao.subscribe({
      next: () => {
        this.loadProdutos(); // recarrega a lista com o que ficou salvo no banco
        this.resetForm();
      },
      error: () => (this.erro = 'Não foi possível salvar o produto. Confira os dados e tente de novo.')
    });
  }

  deleteProduto(id: number) {
    this.productService.deleteProduto(id).subscribe(() => this.loadProdutos());
  }

  editarProduto(produto: Produto) {
    const { id, ...dados } = produto; // separa o id do resto dos campos
    this.novoProduto = dados;
    this.editandoId = id;
  }

  resetForm() {
    this.novoProduto = { ...FORM_VAZIO };
    this.editandoId = null;
    this.erro = '';
  }

  adicionarAoCarrinho(produto: Produto) {
    this.cartService.adicionarAoCarrinho(produto);
  }
}
