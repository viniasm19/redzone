import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';
import { PaymentService } from '../../services/payment.service';
import { precoAtual } from '../../models/produto.model';
import { FormaPagamento, Pedido } from '../../models/pedido.model';

type Etapa = 'revisao' | 'pagamento' | 'aprovado';

@Component({
  selector: 'app-order-confirm',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.scss'
})
// Checkout em 3 passos: revisão, pagamento, aprovado.
export class OrderConfirmComponent implements OnInit, OnDestroy {
  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private orderService = inject(OrderService);
  private paymentService = inject(PaymentService);
  private router = inject(Router);

  itens = this.cartService.itens();
  total = this.cartService.total();

  precoAtual = precoAtual;

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
    const usuario = this.authService.usuarioLogado();
    if (!usuario?.id || this.enviando) {
      return;
    }

    this.enviando = true;
    this.erro = '';

    try {
      this.pedido = await this.orderService.criarPedido(usuario.id, this.itens);
      this.cartService.limparCarrinho();
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
      this.pedido = await this.paymentService.pagar(this.pedido.id, this.formaPagamento);
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
