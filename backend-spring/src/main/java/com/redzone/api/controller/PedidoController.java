package com.redzone.api.controller;

import com.redzone.api.dto.PedidoRequest;
import com.redzone.api.dto.PedidoResponse;
import com.redzone.api.model.ItemPedido;
import com.redzone.api.model.Pedido;
import com.redzone.api.model.Produto;
import com.redzone.api.model.Usuario;
import com.redzone.api.repository.PedidoRepository;
import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Pedidos em /pedidos. O front só manda "quem comprou" e "quais produtos / quantidades";
// preços e total são lidos e calculados aqui, a partir do banco.
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            UsuarioRepository usuarioRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    // POST /pedidos -> 201, 400 (dados inválidos) ou 404 (usuário/produto não existe)
    // @Transactional: ou o pedido e TODOS os itens são salvos, ou nada é salvo.
    @PostMapping
    @Transactional
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findById(request.usuarioId());
        if (usuario.isEmpty()) {
            return naoEncontrado("Usuário não encontrado.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario.get());
        pedido.setData(LocalDateTime.now());

        for (PedidoRequest.ItemRequest itemRequest : request.itens()) {
            Optional<Produto> produto = produtoRepository.findById(itemRequest.produtoId());
            if (produto.isEmpty()) {
                return naoEncontrado("Produto " + itemRequest.produtoId() + " não encontrado.");
            }

            ItemPedido item = new ItemPedido();
            item.setProduto(produto.get());
            item.setQuantidade(itemRequest.quantidade());
            item.setPrecoUnitario(produto.get().getPreco()); // preço vem do banco, não do front
            pedido.adicionarItem(item);
        }

        pedido.calcularTotal();

        // save() no pedido salva também os itens (cascade = ALL)
        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.de(salvo));
    }

    // GET /pedidos/usuario/{usuarioId} -> pedidos do usuário (mais recentes primeiro), ou 404
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

    // GET /pedidos/{id} -> 200 ou 404
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
