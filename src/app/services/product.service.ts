import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Produto } from '../models/produto.model';
import { API_URL } from '../config/api';

// Conversa com a rota /produtos da API (CRUD completo).
// Service = classe que faz um trabalho para várias telas (aqui: falar com a API de produtos).
// @Injectable({ providedIn: 'root' }): o Angular cria UM objeto deste service para o app inteiro,
// e entrega esse mesmo objeto a quem pedir (com inject ou no construtor).
//
// Os métodos daqui devolvem um Observable: a resposta da API chega "depois". Quem chama usa
// .subscribe(...) para dizer o que fazer quando ela chegar.
@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);       // o "carteiro" que faz as requisições HTTP
  private url = `${API_URL}/produtos`;     // http://localhost:8080/produtos

  // GET /produtos: todos os produtos (usado na vitrine, no estoque e no carrinho)
  getProdutos() {
    return this.http.get<Produto[]>(this.url);
  }

  // GET /produtos?promocao=true: só os produtos com preço promocional
  getPromocoes() {
    return this.http.get<Produto[]>(this.url, { params: { promocao: true } });
  }

  // GET /produtos/{id}: busca só um produto (a API responde 404 se ele não existir)
  getProduto(id: number) {
    return this.http.get<Produto>(`${this.url}/${id}`);
  }

  // POST: sem id, porque quem gera o id é o banco de dados.
  // Omit<Produto, 'id'> = "um Produto, mas sem o campo id".
  addProduto(produto: Omit<Produto, 'id'>) {
    return this.http.post<Produto>(this.url, produto);
  }

  // PUT /produtos/{id}: altera um produto que já existe (só o administrador usa)
  updateProduto(produto: Produto) {
    return this.http.put<Produto>(`${this.url}/${produto.id}`, produto);
  }

  // DELETE /produtos/{id}: apaga o produto (a API responde 409 se ele já estiver em algum pedido)
  deleteProduto(id: number) {
    return this.http.delete(`${this.url}/${id}`);
  }
}
