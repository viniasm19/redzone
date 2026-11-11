package com.redzone.api.service;

// Exceção "nossa": é lançada quando alguém tenta tirar do estoque mais do que existe.
// Estende RuntimeException para que o @Transactional desfaça tudo (rollback) quando ela acontece.
// Quem transforma ela em resposta HTTP 409 é o ApiExceptionHandler.
public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
