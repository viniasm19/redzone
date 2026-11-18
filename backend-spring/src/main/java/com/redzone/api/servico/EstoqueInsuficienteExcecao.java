package com.redzone.api.servico;

// Erro quando falta estoque (vira 409).
public class EstoqueInsuficienteExcecao extends RuntimeException {

    public EstoqueInsuficienteExcecao(String mensagem) {
        super(mensagem);
    }
}
