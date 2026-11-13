package com.redzone.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// O que o front ENVIA no POST /pedidos. Só ids e quantidades.
// (record = classe enxuta, só com dados.
public record PedidoRequest(
        @NotNull(message = "Informe o usuário.")
        Long usuarioId,

        // @NotEmpty: a lista não pode ser vazia.
        // @Valid aqui: valida também CADA item da lista (as regras do ItemRequest abaixo).
        @NotEmpty(message = "O pedido precisa ter pelo menos um item.")
        @Valid
        List<ItemRequest> itens
) {
    // Um item do pedido: qual produto e quantas unidades. Record dentro de record.
    // @Min(1): não dá para comprar zero (nem um número negativo de) unidades.
    public record ItemRequest(
            @NotNull(message = "Informe o produto.")
            Long produtoId,

            @NotNull(message = "Informe a quantidade.")
            @Min(value = 1, message = "A quantidade mínima é 1.")
            Integer quantidade
    ) {
    }
}
