package com.redzone.api.controlador;

import com.redzone.api.dto.PagamentoRequisicao;
import com.redzone.api.dto.PedidoResposta;
import com.redzone.api.modelo.Pedido;
import com.redzone.api.modelo.StatusPedido;
import com.redzone.api.repositorio.PedidoRepositorio;
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
public class PagamentoControlador {

    private final PedidoRepositorio pedidoRepositorio;

    public PagamentoControlador(PedidoRepositorio pedidoRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
    }

    @PostMapping
    @Transactional
    // Aprova o pedido (sempre aprova) e guarda a forma.
    public ResponseEntity<?> pagar(@Valid @RequestBody PagamentoRequisicao request) {
        Optional<Pedido> encontrado = pedidoRepositorio.findById(request.pedidoId());
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

        return ResponseEntity.ok(PedidoResposta.de(pedidoRepositorio.save(pedido)));
    }
}
