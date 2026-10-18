import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { HttpClient } from '@angular/common/http';
import { CartService } from './services/cart.service';
import { Produto } from './models/produto.model';
import { Usuario } from './models/usuario.model';
import { API_URL } from './config/api';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'darkshop';
  cartService = inject(CartService);

  private http = inject(HttpClient);
  private API = API_URL;

  produtos: Produto[] = [];
  usuarios: Usuario[] = [];
  carrinho: Produto[] = [];

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.listarProdutos().subscribe(dados => this.produtos = dados);
    this.listarUsuarios().subscribe(dados => this.usuarios = dados);
  }

  adicionarAoCarrinho(produto: Produto) {
    this.carrinho = [...this.carrinho, produto];
  }

  get totalGeral(): number {
    return this.carrinho.reduce((total, p) => total + p.preco, 0);
  }

  listarProdutos() {
    return this.http.get<Produto[]>(`${this.API}/produtos`);
  }

  excluirProduto(id: number) {
    return this.http.delete(`${this.API}/produtos/${id}`);
  }

  listarUsuarios() {
    return this.http.get<Usuario[]>(`${this.API}/usuarios`);
  }

  excluirUsuario(id: number) {
    return this.http.delete(`${this.API}/usuarios/${id}`);
  }

  private obterMaiorId<T extends { id: number }>(itens: T[]): number {
    if (!itens?.length) return 0;
    return itens.reduce((max, item) => (item.id > max ? item.id : max), 0);
  }

  salvarNovoProduto(produto: Omit<Produto, 'id'>) {
    return this.listarProdutos().toPromise().then((lista: any) => {
      const novoId = this.obterMaiorId(lista || []) + 1;
      const payload: Produto = { ...produto, id: novoId };
      return this.http.put(`${this.API}/produtos/${novoId}`, payload).toPromise();
    });
  }

  salvarNovoUsuario(usuario: Omit<Usuario, 'id'>) {
    return this.listarUsuarios().toPromise().then((lista: any) => {
      const novoId = this.obterMaiorId(lista || []) + 1;
      const payload: Usuario = { ...usuario, id: novoId };
      return this.http.put(`${this.API}/usuarios/${novoId}`, payload).toPromise();
    });
  }
}