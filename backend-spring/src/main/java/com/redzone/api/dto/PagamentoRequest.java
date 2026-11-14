package com.redzone.api.dto;

import com.redzone.api.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;

// Pagamento: pedido e forma. Sem dados de cartão.
public record PagamentoRequest(
        @NotNull(message = "Informe o pedido.")
        Long pedidoId,

        @NotNull(message = "Informe a forma de pagamento (CARTAO ou PIX).")
        FormaPagamento formaPagamento
) {
}
