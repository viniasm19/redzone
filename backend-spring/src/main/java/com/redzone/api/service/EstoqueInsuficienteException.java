package com.redzone.api.service;

// Exceção "nossa": é lançada quando alguém tenta tirar do estoque mais do que.
// Estende RuntimeException para que o @Transactional desfaça tudo (rollback).
public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
