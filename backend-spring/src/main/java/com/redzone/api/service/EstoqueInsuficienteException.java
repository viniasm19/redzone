package com.redzone.api.service;

// Erro quando falta estoque (vira 409).
public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
