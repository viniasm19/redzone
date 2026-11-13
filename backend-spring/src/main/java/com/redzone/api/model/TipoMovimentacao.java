package com.redzone.api.model;

// Os tipos de movimentação de estoque.
// enum = um tipo com uma lista FECHADA de valores.
public enum TipoMovimentacao {
    ENTRADA, // chegou mercadoria (reposição feita pelo administrador)
    SAIDA,   // saiu sem ser venda (perda, defeito, ajuste de inventário...)
    VENDA    // saiu porque um cliente comprou (registrada sozinha ao finalizar o pedido)
}
