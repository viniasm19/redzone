package com.redzone.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Quantidade e observação de entrada ou saída.
public record MovimentacaoRequest(
        @NotNull(message = "Informe a quantidade.")
        @Min(value = 1, message = "A quantidade mínima é 1.")
        Integer quantidade,

        @Size(max = 255, message = "A observação pode ter no máximo 255 caracteres.")
        String observacao
) {
}
