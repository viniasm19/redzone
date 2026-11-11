import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Produto } from '../models/produto.model';
import { API_URL } from '../config/api';

// Conversa com a rota /produtos da API (CRUD completo).
@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private url = `${API_URL}/produtos`;

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
  addProduto(produto: Omit<Produto, 'id'>) {
    return this.http.post<Produto>(this.url, produto);
  }

  updateProduto(produto: Produto) {
    return this.http.put<Produto>(`${this.url}/${produto.id}`, produto);
  }

  deleteProduto(id: number) {
    return this.http.delete(`${this.url}/${id}`);
  }
}
