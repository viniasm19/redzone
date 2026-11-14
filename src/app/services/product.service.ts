import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Produto } from '../models/produto.model';
import { API_URL } from '../config/api';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private url = `${API_URL}/produtos`;

  // GET /produtos: todos os produtos.
  getProdutos() {
    return this.http.get<Produto[]>(this.url);
  }

  // GET /produtos?promocao=true.
  getPromocoes() {
    return this.http.get<Produto[]>(this.url, { params: { promocao: true } });
  }

  // GET /produtos/{id}: um produto.
  getProduto(id: number) {
    return this.http.get<Produto>(`${this.url}/${id}`);
  }

  // POST /produtos: cadastra (admin).
  addProduto(produto: Omit<Produto, 'id'>) {
    return this.http.post<Produto>(this.url, produto);
  }

  // PUT /produtos/{id}: edita (admin).
  updateProduto(produto: Produto) {
    return this.http.put<Produto>(`${this.url}/${produto.id}`, produto);
  }

  // DELETE /produtos/{id} (admin).
  deleteProduto(id: number) {
    return this.http.delete(`${this.url}/${id}`);
  }
}
