package com.redzone.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// Pedido que o front envia: usuário e itens.
public record PedidoRequest(
        @NotNull(message = "Informe o usuário.")
        Long usuarioId,

        @NotEmpty(message = "O pedido precisa ter pelo menos um item.")
        @Valid
        List<ItemRequest> itens
) {
    // Um item: produto e quantidade.
    public record ItemRequest(
            @NotNull(message = "Informe o produto.")
            Long produtoId,

            @NotNull(message = "Informe a quantidade.")
            @Min(value = 1, message = "A quantidade mínima é 1.")
            Integer quantidade
    ) {
    }
}
