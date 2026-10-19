import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Produto } from '../models/produto.model';
import { API_URL } from '../config/api';


@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly apiUrl = `${API_URL}/produtos`;

  constructor(private http: HttpClient) {}

  getProdutos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.apiUrl);
  }

  addProduto(produto: Produto): Observable<Produto> {
    return this.http.post<Produto>(this.apiUrl, produto);
  }

  deleteProduto(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  updateProduto(produto: Produto): Observable<Produto> {
    return this.http.put<Produto>(
      `${this.apiUrl}/${produto.id}`,
      produto
    );
  }
}