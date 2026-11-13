package com.redzone.api.model;

// As etapas por que um pedido passa, na ordem.
// É gravado no banco pelo NOME (EnumType.STRING).
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO, // acabou de ser criado: o cliente ainda não pagou
    APROVADO,             // pagamento aprovado (POST /pagamentos)
    EM_SEPARACAO,         // a loja está separando os produtos
    ENVIADO,              // saiu para entrega
    ENTREGUE              // chegou ao cliente
}
