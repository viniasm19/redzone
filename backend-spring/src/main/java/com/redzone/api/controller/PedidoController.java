package com.redzone.api.controller;

import com.redzone.api.dto.PedidoRequest;
import com.redzone.api.dto.PedidoResponse;
import com.redzone.api.model.ItemPedido;
import com.redzone.api.model.Pedido;
import com.redzone.api.model.Produto;
import com.redzone.api.model.StatusPedido;
import com.redzone.api.model.TipoMovimentacao;
import com.redzone.api.model.Usuario;
import com.redzone.api.repository.PedidoRepository;
import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.repository.UsuarioRepository;
import com.redzone.api.service.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Rotas de pedidos.
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    public PedidoController(PedidoRepository pedidoRepository,
                            UsuarioRepository usuarioRepository,
                            ProdutoRepository produtoRepository,
                            EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
    }

    @PostMapping
    @Transactional
    // Cria o pedido. Preço e total são calculados aqui.
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findById(request.usuarioId());
        if (usuario.isEmpty()) {
            return naoEncontrado("Usuário não encontrado.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario.get());
        pedido.setData(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        for (PedidoRequest.ItemRequest itemRequest : request.itens()) {
            Optional<Produto> produto = produtoRepository.findById(itemRequest.produtoId());
            if (produto.isEmpty()) {
                return naoEncontrado("Produto " + itemRequest.produtoId() + " não encontrado.");
            }

            ItemPedido item = new ItemPedido();
            item.setProduto(produto.get());
            item.setQuantidade(itemRequest.quantidade());
            item.setPrecoUnitario(produto.get().getPrecoAtual());
            pedido.adicionarItem(item);

            estoqueService.saida(produto.get(), itemRequest.quantidade(), TipoMovimentacao.VENDA,
                    "Venda para " + usuario.get().getNome());
        }

        pedido.calcularTotal();

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.de(salvo));
    }

    // Pedidos de um usuário (Minha Área).
    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return naoEncontrado("Usuário não encontrado.");
        }

        List<PedidoResponse> pedidos = pedidoRepository.findByUsuarioIdOrderByDataDesc(usuarioId)
                .stream()
                .map(PedidoResponse::de)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    // Um pedido pelo id.
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(PedidoResponse.de(p)))
                .orElseGet(() -> naoEncontrado("Pedido não encontrado."));
    }

    private ResponseEntity<Map<String, String>> naoEncontrado(String mensagem) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", mensagem));
    }
}
