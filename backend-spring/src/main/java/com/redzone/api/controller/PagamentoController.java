package com.redzone.api.controller;

import com.redzone.api.dto.PagamentoRequest;
import com.redzone.api.dto.PedidoResponse;
import com.redzone.api.model.Pedido;
import com.redzone.api.model.StatusPedido;
import com.redzone.api.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

// Pagamento FICTÍCIO (mock) em /pagamentos: não fala com banco, operadora de cartão nem Pix de verdade.
// Ele sempre aprova e só muda o pedido de AGUARDANDO_PAGAMENTO para APROVADO, guardando a forma de pagamento.
// @RestController: esta classe recebe requisições HTTP e devolve respostas em JSON.
//   (É um @Controller + @ResponseBody: o que os métodos retornam vira JSON automaticamente.)
// @RequestMapping("..."): o começo do endereço de todas as rotas desta classe.
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PedidoRepository pedidoRepository;

    // Injeção de dependência: o Spring entrega o repository de pedidos pronto.
    public PagamentoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // POST /pagamentos -> 200 com o pedido atualizado
    //                     400 (faltou pedidoId ou formaPagamento, ou forma inválida)
    //                     404 (pedido não existe)
    //                     409 (o pedido já foi pago: não deixa pagar duas vezes)
    @PostMapping
    @Transactional
    // Recebe: o PagamentoRequest (pedidoId + formaPagamento), já validado pelo @Valid.
    // Devolve: o pedido atualizado (status APROVADO e a forma de pagamento).
    public ResponseEntity<?> pagar(@Valid @RequestBody PagamentoRequest request) {
        // 1) procura o pedido; se não existe, 404
        Optional<Pedido> encontrado = pedidoRepository.findById(request.pedidoId());
        if (encontrado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Pedido não encontrado."));
        }

        // 2) só paga quem está esperando pagamento; um pedido já pago responde 409 (não cobra duas vezes)
        Pedido pedido = encontrado.get();
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensagem", "Este pedido já foi pago."));
        }

        // 3) Mock: aprova sempre. Num sistema real, aqui entraria a chamada ao serviço de pagamento.
        pedido.setFormaPagamento(request.formaPagamento());
        pedido.setStatus(StatusPedido.APROVADO);

        // 4) salva e responde 200 com o pedido convertido em DTO.
        // @Transactional: tudo aqui é uma operação só no banco.
        // Dentro do @Transactional, o Hibernate grava a mudança sozinho no fim do método;
        // o save() deixa isso explícito para quem está lendo.
        return ResponseEntity.ok(PedidoResponse.de(pedidoRepository.save(pedido)));
    }
}
