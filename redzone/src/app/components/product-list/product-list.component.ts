import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductItemComponent } from '../product-item/product-item.component';
import { Produto } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ProductItemComponent, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})

export class ProductListComponent implements OnInit {

  todos: Produto[] = [];
  produtos: Produto[] = [];

  categorias: string[] = [
    'Áudio',
    'Móveis',
    'Hardware',
    'Acessórios'
  ];

  novoProduto: Produto = {
    id: 0,
    nome: '',
    preco: 0,
    imagem: '',
    categoria: ''
  };

   @Input() set categoriaSelecionada(cat: string) {  // 👈 recebe da home
    if (!cat || cat === 'Todos') {
      this.produtos = this.todos;
    } else {
      this.produtos = this.todos.filter(p => p.categoria === cat);
    }
  }


  editandoId: number | null = null;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private auth: AuthService

  ) { }

  //Acesso apenas para ADMIN
  isAdmin(): boolean {
  return this.auth.isAdmin();
}

  ngOnInit() {
    this.loadProdutos();
  }

  loadProdutos() {
    this.productService.getProdutos()
      .subscribe(data => {
        this.todos = data;      
        this.produtos = data; 
      });
  }

  trackById(index: number, item: Produto) {
    return item.id;
  }

  addProduto() {

    if (this.editandoId) {

      const produtoAtualizado: Produto = {
        ...this.novoProduto,
        id: this.editandoId
      };

      this.productService.updateProduto(produtoAtualizado)
        .subscribe(() => {
          this.loadProdutos();
          this.resetForm();
        });

    } else {

      const { id, ...produtoSemId } = this.novoProduto;
  this.productService.addProduto(produtoSemId as Produto)
    .subscribe(() => {
      this.loadProdutos();
      this.resetForm();
    });
  }
  }

  deleteProduto(id: number) {
    this.productService.deleteProduto(id)
      .subscribe(() => this.loadProdutos());
  }

  editarProduto(produto: Produto) {
    this.novoProduto = { ...produto };
    this.editandoId = produto.id;
  }

  resetForm() {
    this.novoProduto = {
      id: 0,
      nome: '',
      preco: 0,
      imagem: '',
      categoria: ''
    };

    this.editandoId = null;
  }

  adicionarAoCarrinho(produto: Produto) {
    this.cartService.adicionarAoCarrinho(produto);
  }
}