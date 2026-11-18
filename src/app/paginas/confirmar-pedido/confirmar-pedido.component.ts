import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CarrinhoService } from '../../servicos/carrinho.service';
import { AutenticacaoService } from '../../servicos/autenticacao.service';
import { PedidoService } from '../../servicos/pedido.service';
import { PagamentoService } from '../../servicos/pagamento.service';
import { FormaPagamento, Pedido } from '../../modelos/pedido.model';

type Etapa = 'revisao' | 'pagamento' | 'aprovado';

@Component({
  selector: 'app-confirmar-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './confirmar-pedido.component.html',
  styleUrl: './confirmar-pedido.component.scss'
})
// Checkout em 3 passos: revisão, pagamento, aprovado.
export class ConfirmarPedidoComponent implements OnInit, OnDestroy {
  private carrinhoService = inject(CarrinhoService);
  private autenticacaoService = inject(AutenticacaoService);
  private pedidoService = inject(PedidoService);
  private pagamentoService = inject(PagamentoService);
  private router = inject(Router);

  itens = this.carrinhoService.itens();
  total = this.carrinhoService.total();

  etapa: Etapa = 'revisao';
  pedido: Pedido | null = null;

  enviando = false;
  erro = '';

  formaPagamento: FormaPagamento = 'PIX';

  cartao = { numero: '', validade: '', cvv: '' };

  tentouPagar = false;
  pixCopiado = false;

  private timer?: ReturnType<typeof setTimeout>;

  ngOnInit() {
    if (this.itens.length === 0) {
      this.router.navigate(['/']);
    }
  }

  ngOnDestroy() {
    clearTimeout(this.timer);
  }

  // Passo 1: cria o pedido.
  async confirmar() {
    const usuario = this.autenticacaoService.usuarioLogado();
    if (!usuario?.id || this.enviando) {
      return;
    }

    this.enviando = true;
    this.erro = '';

    try {
      this.pedido = await this.pedidoService.criarPedido(usuario.id, this.itens);
      this.carrinhoService.limparCarrinho();
      this.etapa = 'pagamento';
    } catch (e) {
      if (e instanceof HttpErrorResponse && e.status === 409 && e.error?.mensagem) {
        this.erro = `${e.error.mensagem} Volte ao carrinho e ajuste a quantidade.`;
      } else {
        this.erro = 'Não foi possível finalizar o pedido. Tente novamente.';
      }
    } finally {
      this.enviando = false;
    }
  }



  // Valida o cartão (só o formato).
  get erroNumero(): string {
    const digitos = this.cartao.numero.replace(/\s/g, '');
    return /^\d{13,19}$/.test(digitos) ? '' : 'Digite um número de cartão com 13 a 19 dígitos.';
  }

  get erroValidade(): string {
    const partes = /^(0[1-9]|1[0-2])\/(\d{2})$/.exec(this.cartao.validade.trim());
    if (!partes) {
      return 'Use o formato MM/AA (ex.: 08/29).';
    }
    const mes = Number(partes[1]);
    const ano = 2000 + Number(partes[2]);
    return new Date(ano, mes, 1) > new Date() ? '' : 'Este cartão está vencido.';
  }

  get erroCvv(): string {
    return /^\d{3,4}$/.test(this.cartao.cvv.trim()) ? '' : 'O CVV tem 3 ou 4 dígitos.';
  }

  get cartaoValido() {
    return !this.erroNumero && !this.erroValidade && !this.erroCvv;
  }

  get codigoPix() {
    const id = String(this.pedido?.id ?? 0).padStart(6, '0');
    return `PIX-FICTICIO-REDZONE-PEDIDO-${id}-NAO-PAGAR`;
  }

  async copiarCodigoPix() {
    try {
      await navigator.clipboard.writeText(this.codigoPix);
      this.pixCopiado = true;
    } catch {
    }
  }

  // Passo 2: envia o pagamento.
  async pagar() {
    this.tentouPagar = true;
    if (!this.pedido || this.enviando) {
      return;
    }
    if (this.formaPagamento === 'CARTAO' && !this.cartaoValido) {
      return;
    }

    this.enviando = true;
    this.erro = '';

    try {
      this.pedido = await this.pagamentoService.pagar(this.pedido.id, this.formaPagamento);
      this.cartao = { numero: '', validade: '', cvv: '' };
      this.etapa = 'aprovado';
      this.timer = setTimeout(() => this.router.navigate(['/order-success']), 1500);
    } catch (e) {
      this.erro = e instanceof HttpErrorResponse && e.error?.mensagem
        ? e.error.mensagem
        : 'Não foi possível processar o pagamento. Tente novamente.';
    } finally {
      this.enviando = false;
    }
  }

  voltar() {
    this.router.navigate(['/cart']);
  }
}
