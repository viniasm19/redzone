import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ItemProdutoComponent } from '../item-produto/item-produto.component';
import { Produto } from '../../modelos/produto.model';
import { ProdutoService } from '../../servicos/produto.service';
import { CarrinhoService } from '../../servicos/carrinho.service';
import { AutenticacaoService } from '../../servicos/autenticacao.service';

const FORM_VAZIO: Omit<Produto, 'id'> = {
  nome: '', preco: 0, imagem: '', categoria: '', estoque: 0
};

@Component({
  selector: 'app-lista-produtos',
  standalone: true,
  imports: [CommonModule, ItemProdutoComponent, FormsModule, RouterModule],
  templateUrl: './lista-produtos.component.html',
  styleUrls: ['./lista-produtos.component.scss']
})
// Vitrine e formulário de produto (admin).
export class ListaProdutosComponent implements OnInit {
  private produtoService = inject(ProdutoService);
  private carrinhoService = inject(CarrinhoService);
  private auth = inject(AutenticacaoService);

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
    this.produtoService.getProdutos().subscribe(dados => {
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

    const requisicao = this.editandoId
      ? this.produtoService.updateProduto({ ...p, id: this.editandoId })
      : this.produtoService.addProduto(p);

    requisicao.subscribe({
      next: () => {
        this.loadProdutos();
        this.resetForm();
      },
      error: () => (this.erro = 'Não foi possível salvar o produto. Confira os dados e tente de novo.')
    });
  }

  deleteProduto(id: number) {
    this.erro = '';
    this.produtoService.deleteProduto(id).subscribe({
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
    this.carrinhoService.adicionarAoCarrinho(produto);
  }
}
