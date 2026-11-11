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

// Os 3 passos desta tela:
//   revisao   -> a pessoa confere os itens e confirma: o pedido é CRIADO (status AGUARDANDO_PAGAMENTO)
//   pagamento -> escolhe cartão ou Pix e paga (POST /pagamentos, fictício: sempre aprova)
//   aprovado  -> mostra "Pagamento aprovado" e vai sozinho para /order-success
type Etapa = 'revisao' | 'pagamento' | 'aprovado';

@Component({
  selector: 'app-order-confirm',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.scss'
})
// O HTML espera daqui: etapa (qual passo mostrar), itens/total (revisão), pedido (pagamento),
// formaPagamento e cartao (ligados aos campos), os erros de cada campo do cartão, codigoPix,
// enviando/erro/tentouPagar/pixCopiado (estado da tela) e os métodos confirmar, pagar,
// copiarCodigoPix e voltar.
// implements OnInit, OnDestroy: a classe tem ngOnInit (roda ao abrir a tela) e ngOnDestroy (ao sair).
export class OrderConfirmComponent implements OnInit, OnDestroy {
  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private orderService = inject(OrderService);
  private paymentService = inject(PaymentService);
  private router = inject(Router);

  // "Foto" do carrinho ao entrar na tela: continua na tela mesmo depois de limpar o carrinho
  itens = this.cartService.itens();
  total = this.cartService.total();

  precoAtual = precoAtual; // para o HTML calcular o subtotal com o preço promocional

  etapa: Etapa = 'revisao';
  pedido: Pedido | null = null; // o pedido criado no passo 1 (o total dele é o oficial, calculado no back)

  enviando = false; // true enquanto espera a API: desabilita o botão (evita pedido/pagamento duplicado)
  erro = '';

  // ---------- Pagamento ----------
  formaPagamento: FormaPagamento = 'PIX';

  // Dados do cartão: só existem AQUI, na memória da tela, para validar o formato.
  // Nunca são enviados para a API nem guardados no navegador (localStorage/sessionStorage).
  cartao = { numero: '', validade: '', cvv: '' };

  tentouPagar = false; // só mostra os erros do cartão depois do primeiro clique em "Pagar"
  pixCopiado = false;

  // Guarda o "relógio" do redirecionamento, para poder cancelá-lo se a pessoa sair antes
  private timer?: ReturnType<typeof setTimeout>;

  // Roda quando a tela abre
  ngOnInit() {
    if (this.itens.length === 0) {
      this.router.navigate(['/']); // sem itens não há o que confirmar
    }
  }

  // Roda quando a tela fecha: cancela o redirecionamento pendente (se houver)
  ngOnDestroy() {
    clearTimeout(this.timer);
  }

  // Passo 1: cria o pedido (AGUARDANDO_PAGAMENTO) e passa para a escolha do pagamento
  async confirmar() {
    // 1) pega quem está logado; sem usuário (ou se já está enviando), não faz nada
    const usuario = this.authService.usuarioLogado();
    if (!usuario?.id || this.enviando) {
      return;
    }

    // 2) trava o botão e apaga o erro anterior
    this.enviando = true;
    this.erro = '';

    try {
      // 3) cria o pedido na API (POST /pedidos) com os itens da "foto" do carrinho
      this.pedido = await this.orderService.criarPedido(usuario.id, this.itens); // salva o pedido no banco
      // O pedido já existe (e o estoque já foi reservado): o carrinho pode ser limpo.
      // Se o pagamento falhar, a pessoa tenta de novo aqui mesmo, sem criar outro pedido.
      this.cartService.limparCarrinho();
      // 4) mostra o passo 2 (escolha do pagamento)
      this.etapa = 'pagamento';
    } catch (e) {
      // 409 = algum produto acabou enquanto a pessoa comprava. O back-end diz qual na "mensagem".
      if (e instanceof HttpErrorResponse && e.status === 409 && e.error?.mensagem) {
        this.erro = `${e.error.mensagem} Volte ao carrinho e ajuste a quantidade.`;
      } else {
        this.erro = 'Não foi possível finalizar o pedido. Tente novamente.';
      }
    } finally {
      // 5) "finally" roda dando certo ou errado: destrava o botão
      this.enviando = false;
    }
  }

  // ---------- Validação do cartão (só o FORMATO; não existe cartão de verdade aqui) ----------

  // "get" = propriedade calculada: o HTML lê erroNumero como um campo, mas o valor é recalculado
  // a cada vez, a partir do que está digitado. Devolve '' (sem erro) ou a mensagem do erro.

  // Número: de 13 a 19 dígitos (espaços são ignorados)
  get erroNumero(): string {
    const digitos = this.cartao.numero.replace(/\s/g, '');
    return /^\d{13,19}$/.test(digitos) ? '' : 'Digite um número de cartão com 13 a 19 dígitos.';
  }

  // Validade: MM/AA e não vencida
  get erroValidade(): string {
    // 1) confere o formato MM/AA com uma expressão regular (mês de 01 a 12, barra, 2 dígitos)
    const partes = /^(0[1-9]|1[0-2])\/(\d{2})$/.exec(this.cartao.validade.trim());
    if (!partes) {
      return 'Use o formato MM/AA (ex.: 08/29).';
    }
    // 2) separa o mês e o ano ("29" vira 2029)
    const mes = Number(partes[1]);
    const ano = 2000 + Number(partes[2]);
    // 3) compara com hoje
    // new Date(ano, mes, 1) = primeiro dia do mês SEGUINTE: o cartão vale até o fim do mês da validade
    return new Date(ano, mes, 1) > new Date() ? '' : 'Este cartão está vencido.';
  }

  // CVV: 3 ou 4 dígitos
  get erroCvv(): string {
    return /^\d{3,4}$/.test(this.cartao.cvv.trim()) ? '' : 'O CVV tem 3 ou 4 dígitos.';
  }

  // true quando os 3 campos do cartão estão certos
  get cartaoValido() {
    return !this.erroNumero && !this.erroValidade && !this.erroCvv;
  }

  // Código "copia e cola" do Pix: FICTÍCIO, só para ilustrar
  get codigoPix() {
    const id = String(this.pedido?.id ?? 0).padStart(6, '0');
    return `PIX-FICTICIO-REDZONE-PEDIDO-${id}-NAO-PAGAR`;
  }

  // Botão "Copiar código": coloca o código Pix na área de transferência (Ctrl+V)
  async copiarCodigoPix() {
    try {
      await navigator.clipboard.writeText(this.codigoPix);
      this.pixCopiado = true;
    } catch {
      // sem permissão para a área de transferência: a pessoa pode selecionar e copiar o texto
    }
  }

  // Passo 2: "paga" (POST /pagamentos). Só vão o id do pedido e a forma de pagamento.
  async pagar() {
    // 1) a partir de agora, os erros dos campos do cartão aparecem na tela
    this.tentouPagar = true;
    // 2) sem pedido (ou já enviando): não faz nada
    if (!this.pedido || this.enviando) {
      return;
    }
    // 3) cartão com campo errado: não chama a API
    if (this.formaPagamento === 'CARTAO' && !this.cartaoValido) {
      return; // os erros de cada campo aparecem na tela
    }

    // 4) trava o botão
    this.enviando = true;
    this.erro = '';

    try {
      // 5) POST /pagamentos (só o id do pedido e a forma) e guarda o pedido atualizado
      this.pedido = await this.paymentService.pagar(this.pedido.id, this.formaPagamento);
      this.cartao = { numero: '', validade: '', cvv: '' }; // apaga os dados do cartão da memória
      // 6) mostra o passo 3
      this.etapa = 'aprovado';
      // Mostra "Pagamento aprovado" por um instante e segue para a tela de sucesso
      this.timer = setTimeout(() => this.router.navigate(['/order-success']), 1500);
    } catch (e) {
      // 409 = o pedido já estava pago (ex.: clicou duas vezes em abas diferentes)
      this.erro = e instanceof HttpErrorResponse && e.error?.mensagem
        ? e.error.mensagem
        : 'Não foi possível processar o pagamento. Tente novamente.';
    } finally {
      // 7) destrava o botão
      this.enviando = false;
    }
  }

  // Botão "← Voltar" da revisão: volta ao carrinho
  voltar() {
    this.router.navigate(['/cart']);
  }
}
