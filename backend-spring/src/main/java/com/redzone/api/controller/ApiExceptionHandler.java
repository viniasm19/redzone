package com.redzone.api.controller;

import com.redzone.api.service.EstoqueInsuficienteException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

// Tratamento de ERROS da API inteira, num lugar só.
//
// @RestControllerAdvice: "conselheiro" de todos os @RestController. Quando um método de qualquer controller
// lança uma exceção (um erro), o Spring procura aqui um método que saiba tratá-la.
// @ExceptionHandler(TipoDoErro.class): diz qual tipo de erro cada método trata.
// Assim os controllers não precisam de try/catch em todo lugar, e o front sempre recebe um JSON com o motivo.
@RestControllerAdvice
public class ApiExceptionHandler {

    // Rede de segurança: se o banco barrar um e-mail repetido (unique), responde 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflito(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Operação não permitida: registro duplicado ou em uso (ex.: produto ou usuário que já tem pedidos)."));
    }

    // Erros do @Valid: 400 com a lista de campos inválidos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        // 1) cria um "dicionário" campo -> mensagem (LinkedHashMap mantém a ordem dos campos)
        Map<String, String> erros = new LinkedHashMap<>();
        // 2) para cada campo que falhou na validação, guarda o nome do campo e a mensagem da anotação
        e.getBindingResult().getFieldErrors()
                .forEach(erro -> erros.put(erro.getField(), erro.getDefaultMessage()));
        // 3) responde 400 (Bad Request = "pedido mal feito"), ex.: {"preco": "Informe o preço."}
        return ResponseEntity.badRequest().body(erros);
    }

    // Tentou tirar do estoque mais do que existe: 409 com a mensagem explicando qual produto
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<Map<String, String>> estoqueInsuficiente(EstoqueInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", e.getMessage()));
    }

    // Regra de negócio violada (ex.: quantidade zero): 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
    }
}
