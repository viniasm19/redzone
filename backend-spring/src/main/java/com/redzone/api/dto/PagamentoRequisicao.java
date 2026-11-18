package com.redzone.api.dto;

import com.redzone.api.modelo.FormaPagamento;
import jakarta.validation.constraints.NotNull;

// Pagamento: pedido e forma. Sem dados de cartão.
public record PagamentoRequisicao(
        @NotNull(message = "Informe o pedido.")
        Long pedidoId,

        @NotNull(message = "Informe a forma de pagamento (CARTAO ou PIX).")
        FormaPagamento formaPagamento
) {
}
