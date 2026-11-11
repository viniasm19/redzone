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
// Os detalhes (marca, garantia, especificações, preço promocional) são opcionais e começam vazios (null).
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
// Vitrine de produtos + formulário do administrador. Aparece dentro da Home (e na rota /products).
// O HTML espera daqui: produtos (os cards), isAdmin() (mostrar botões de admin), novoProduto/editandoId/erro
// (o formulário), categorias (o select) e os métodos dos botões.
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

  // Roda quando a vitrine aparece: busca os produtos na API
  ngOnInit() {
    this.loadProdutos();
  }

  // Só o administrador vê os botões de editar, excluir e o formulário
  isAdmin() {
    return this.auth.isAdmin();
  }

  // GET /produtos e, quando a resposta chegar (subscribe), guarda tudo e aplica o filtro da categoria
  loadProdutos() {
    this.productService.getProdutos().subscribe(dados => {
      this.todos = dados;
      this.filtrar();
    });
  }

  // "Todos": mostra a lista inteira; senão, só os produtos da categoria escolhida
  private filtrar() {
    this.produtos =
      this.categoria === 'Todos' ? this.todos : this.todos.filter(p => p.categoria === this.categoria);
  }

  // Ajuda o *ngFor a reaproveitar os cards (pelo id) em vez de redesenhar todos quando a lista muda
  trackById(_: number, item: Produto) {
    return item.id;
  }

  // Botão do formulário: editando -> PUT (atualiza); senão -> POST (cria)
  addProduto() {
    // 1) apaga o erro anterior; "p" é só um apelido curto para o formulário
    this.erro = '';
    const p = this.novoProduto;

    // 2) confere os campos obrigatórios (a API confere de novo, mas assim o admin vê o erro na hora)
    if (!p.nome.trim() || !(p.preco > 0) || !p.categoria) {
      this.erro = 'Preencha nome, preço (maior que zero) e categoria.';
      return;
    }

    if (!this.editandoId && !(Number.isInteger(p.estoque) && p.estoque >= 0)) {
      this.erro = 'O estoque inicial deve ser um número inteiro, zero ou maior.';
      return;
    }

    // Campo de número vazio chega como null (ou ''), e isso significa "sem promoção"
    const promocional = p.precoPromocional === null || p.precoPromocional === undefined || String(p.precoPromocional) === ''
      ? null
      : Number(p.precoPromocional);

    if (promocional !== null && !(promocional > 0 && promocional < p.preco)) {
      this.erro = 'O preço promocional deve ser maior que zero e menor que o preço normal (ou deixe vazio).';
      return;
    }

    // Texto só com espaços vira null: assim a tela de detalhe não mostra um título vazio
    const dados: Omit<Produto, 'id'> = {
      ...p,
      marca: this.textoOuNull(p.marca),
      garantia: this.textoOuNull(p.garantia),
      especificacoes: this.textoOuNull(p.especificacoes),
      precoPromocional: promocional
    };

    // 3) escolhe a chamada: PUT se está editando, POST se é um produto novo
    const requisicao = this.editandoId
      ? this.productService.updateProduto({ ...dados, id: this.editandoId })
      : this.productService.addProduto(dados);

    // 4) envia; quando der certo (next), recarrega a lista e limpa o formulário; se der errado (error), avisa
    requisicao.subscribe({
      next: () => {
        this.loadProdutos(); // recarrega a lista com o que ficou salvo no banco
        this.resetForm();
      },
      error: () => (this.erro = 'Não foi possível salvar o produto. Confira os dados e tente de novo.')
    });
  }

  // Recebe: um texto (ou nada). Devolve: o texto sem espaços nas pontas, ou null se ficar vazio.
  private textoOuNull(texto?: string | null): string | null {
    const limpo = (texto ?? '').trim();
    return limpo ? limpo : null;
  }

  // Botão "Excluir" (admin): DELETE /produtos/{id} e recarrega a lista
  deleteProduto(id: number) {
    this.erro = '';
    this.productService.deleteProduto(id).subscribe({
      next: () => this.loadProdutos(),
      // 409 = produto que já está em algum pedido não pode ser excluído
      error: () => (this.erro = 'Não foi possível excluir: o produto já faz parte de algum pedido.')
    });
  }

  // Botão "Editar" (admin): copia o produto para o formulário
  editarProduto(produto: Produto) {
    const { id, ...dados } = produto; // separa o id do resto dos campos
    this.novoProduto = dados;
    this.editandoId = id;
  }

  // Limpa o formulário e sai do modo de edição
  resetForm() {
    this.novoProduto = { ...FORM_VAZIO };
    this.editandoId = null;
    this.erro = '';
  }

  // O card avisou (evento addToCart) que o botão "Adicionar" foi clicado: repassa ao CartService
  adicionarAoCarrinho(produto: Produto) {
    this.cartService.adicionarAoCarrinho(produto);
  }
}
