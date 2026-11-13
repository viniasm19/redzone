package com.redzone.api.dto;

import com.redzone.api.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;

// O que o front ENVIA no POST /pagamentos. (record: classe só de dados.
// De propósito, NÃO existe nenhum campo de cartão (número, validade, CVV) aqui.
public record PagamentoRequest(
        @NotNull(message = "Informe o pedido.")
        Long pedidoId,

        @NotNull(message = "Informe a forma de pagamento (CARTAO ou PIX).")
        FormaPagamento formaPagamento
) {
}
