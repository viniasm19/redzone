import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProductItemComponent } from '../product-item/product-item.component';
import { Produto } from '../../models/produto.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

const FORM_VAZIO: Omit<Produto, 'id'> = {
  nome: '', preco: 0, imagem: '', categoria: '', estoque: 0,
  marca: null, garantia: null, especificacoes: null, precoPromocional: null
};

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductItemComponent, FormsModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
// Vitrine e formulário de produto (admin).
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private auth = inject(AuthService);

  todos: Produto[] = [];
  produtos: Produto[] = [];
  categoria = 'Todos';
  categorias = ['Áudio', 'Móveis', 'Hardware', 'Acessórios'];

  novoProduto: Omit<Produto, 'id'> = { ...FORM_VAZIO };
  editandoId: number | null = null;
  erro = '';

  @Input() set categoriaSelecionada(cat: string) {
    this.categoria = cat || 'Todos';
    this.filtrar();
  }

  ngOnInit() {
    this.loadProdutos();
  }

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

  // Cadastra ou edita produto (admin).
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

    const promocional = p.precoPromocional === null || p.precoPromocional === undefined || String(p.precoPromocional) === ''
      ? null
      : Number(p.precoPromocional);

    if (promocional !== null && !(promocional > 0 && promocional < p.preco)) {
      this.erro = 'O preço promocional deve ser maior que zero e menor que o preço normal (ou deixe vazio).';
      return;
    }

    const dados: Omit<Produto, 'id'> = {
      ...p,
      marca: this.textoOuNull(p.marca),
      garantia: this.textoOuNull(p.garantia),
      especificacoes: this.textoOuNull(p.especificacoes),
      precoPromocional: promocional
    };

    const requisicao = this.editandoId
      ? this.productService.updateProduto({ ...dados, id: this.editandoId })
      : this.productService.addProduto(dados);

    requisicao.subscribe({
      next: () => {
        this.loadProdutos();
        this.resetForm();
      },
      error: () => (this.erro = 'Não foi possível salvar o produto. Confira os dados e tente de novo.')
    });
  }

  private textoOuNull(texto?: string | null): string | null {
    const limpo = (texto ?? '').trim();
    return limpo ? limpo : null;
  }

  deleteProduto(id: number) {
    this.erro = '';
    this.productService.deleteProduto(id).subscribe({
      next: () => this.loadProdutos(),
      error: () => (this.erro = 'Não foi possível excluir: o produto já faz parte de algum pedido.')
    });
  }

  editarProduto(produto: Produto) {
    const { id, ...dados } = produto;
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
