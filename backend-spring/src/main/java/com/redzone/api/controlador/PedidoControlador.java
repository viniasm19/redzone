package com.redzone.api.controlador;

import com.redzone.api.dto.PedidoRequisicao;
import com.redzone.api.dto.PedidoResposta;
import com.redzone.api.modelo.ItemPedido;
import com.redzone.api.modelo.Pedido;
import com.redzone.api.modelo.Produto;
import com.redzone.api.modelo.StatusPedido;
import com.redzone.api.modelo.TipoMovimentacao;
import com.redzone.api.modelo.Usuario;
import com.redzone.api.repositorio.PedidoRepositorio;
import com.redzone.api.repositorio.ProdutoRepositorio;
import com.redzone.api.repositorio.UsuarioRepositorio;
import com.redzone.api.servico.EstoqueServico;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Rotas de pedidos.
@RestController
@RequestMapping("/pedidos")
public class PedidoControlador {

    private final PedidoRepositorio pedidoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ProdutoRepositorio produtoRepositorio;
    private final EstoqueServico estoqueServico;

    public PedidoControlador(PedidoRepositorio pedidoRepositorio,
                            UsuarioRepositorio usuarioRepositorio,
                            ProdutoRepositorio produtoRepositorio,
                            EstoqueServico estoqueServico) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.produtoRepositorio = produtoRepositorio;
        this.estoqueServico = estoqueServico;
    }

    @PostMapping
    @Transactional
    // Cria o pedido. Preço e total são calculados aqui.
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoRequisicao request) {
        Optional<Usuario> usuario = usuarioRepositorio.findById(request.usuarioId());
        if (usuario.isEmpty()) {
            return naoEncontrado("Usuário não encontrado.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario.get());
        pedido.setData(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // 1º confere se todos os produtos existem, antes de mexer no estoque.
        List<Produto> produtos = new ArrayList<>();
        for (PedidoRequisicao.ItemRequisicao itemRequest : request.itens()) {
            Optional<Produto> produto = produtoRepositorio.findById(itemRequest.produtoId());
            if (produto.isEmpty()) {
                return naoEncontrado("Produto " + itemRequest.produtoId() + " não encontrado.");
            }
            produtos.add(produto.get());
        }

        // 2º monta os itens e dá baixa (se faltar estoque, a exceção desfaz tudo).
        for (int i = 0; i < produtos.size(); i++) {
            Produto produto = produtos.get(i);
            int quantidade = request.itens().get(i).quantidade();

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());
            pedido.adicionarItem(item);

            estoqueServico.saida(produto, quantidade, TipoMovimentacao.VENDA,
                    "Venda para " + usuario.get().getNome());
        }

        pedido.calcularTotal();

        Pedido salvo = pedidoRepositorio.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResposta.de(salvo));
    }

    private ResponseEntity<Map<String, String>> naoEncontrado(String mensagem) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", mensagem));
    }
}
