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

// Pagamento fictício: POST /pagamentos.
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PedidoRepository pedidoRepository;

    public PagamentoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping
    @Transactional
    // Aprova o pedido (sempre aprova) e guarda a forma.
    public ResponseEntity<?> pagar(@Valid @RequestBody PagamentoRequest request) {
        Optional<Pedido> encontrado = pedidoRepository.findById(request.pedidoId());
        if (encontrado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Pedido não encontrado."));
        }

        Pedido pedido = encontrado.get();
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensagem", "Este pedido já foi pago."));
        }

        pedido.setFormaPagamento(request.formaPagamento());
        pedido.setStatus(StatusPedido.APROVADO);

        return ResponseEntity.ok(PedidoResponse.de(pedidoRepository.save(pedido)));
    }
}
