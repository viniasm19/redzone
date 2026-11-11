package com.redzone.api.dto;

import com.redzone.api.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;

// O que o front ENVIA no POST /pagamentos.
// De propósito, NÃO existe nenhum campo de cartão (número, validade, CVV) aqui: o pagamento é fictício
// e esses dados nunca devem chegar ao back-end nem ser gravados. Se o front mandar algum campo a mais,
// o Jackson simplesmente o descarta (não há onde colocá-lo neste record).
public record PagamentoRequest(
        @NotNull(message = "Informe o pedido.")
        Long pedidoId,

        @NotNull(message = "Informe a forma de pagamento (CARTAO ou PIX).")
        FormaPagamento formaPagamento
) {
}
