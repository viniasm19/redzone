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

// Pedidos em /pedidos. O front só manda "quem comprou" e "quais produtos / quantidades";
// preços e total são lidos e calculados aqui, a partir do banco.
// @RestController: esta classe recebe requisições HTTP e devolve respostas em JSON.
//   (É um @Controller + @ResponseBody: o que os métodos retornam vira JSON automaticamente.)
// @RequestMapping("..."): o começo do endereço de todas as rotas desta classe.
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    // Injeção de dependência: o Spring entrega os 3 repositories e o EstoqueService prontos.
    public PedidoController(PedidoRepository pedidoRepository,
                            UsuarioRepository usuarioRepository,
                            ProdutoRepository produtoRepository,
                            EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
    }

    // POST /pedidos -> 201, 400 (dados inválidos), 404 (usuário/produto não existe)
    //                  ou 409 (algum produto não tem estoque suficiente)
    // @Transactional: ou o pedido, TODOS os itens e TODAS as baixas de estoque são salvos, ou nada é salvo.
    // Ex.: se o 1º item tem estoque e o 2º não, a baixa do 1º é desfeita (rollback).
    @PostMapping
    @Transactional
    // Recebe: o PedidoRequest (usuarioId + itens com produtoId e quantidade), já validado pelo @Valid.
    // Devolve: o pedido salvo (PedidoResponse), com preços e total calculados aqui.
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoRequest request) {
        // 1) confere se o usuário existe
        Optional<Usuario> usuario = usuarioRepository.findById(request.usuarioId());
        if (usuario.isEmpty()) {
            return naoEncontrado("Usuário não encontrado.");
        }

        // 2) cria o pedido em memória (ainda não está no banco): de quem é, quando foi feito e o status
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario.get());
        pedido.setData(LocalDateTime.now());
        // Todo pedido nasce esperando o pagamento. Quem muda para APROVADO é o POST /pagamentos.
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // 3) para cada item que o front mandou...
        for (PedidoRequest.ItemRequest itemRequest : request.itens()) {
            // 3a) confere se o produto existe
            Optional<Produto> produto = produtoRepository.findById(itemRequest.produtoId());
            if (produto.isEmpty()) {
                return naoEncontrado("Produto " + itemRequest.produtoId() + " não encontrado.");
            }

            // 3b) monta o item: produto, quantidade e o preço LIDO DO BANCO
            ItemPedido item = new ItemPedido();
            item.setProduto(produto.get());
            item.setQuantidade(itemRequest.quantidade());
            // Preço vem do banco, não do front. Se o produto está em promoção, cobra o preço promocional.
            item.setPrecoUnitario(produto.get().getPrecoAtual());
            // 3c) coloca o item dentro do pedido
            pedido.adicionarItem(item);

            // 3d) Dá baixa no estoque. Se faltar unidade, lança EstoqueInsuficienteException -> 409
            estoqueService.saida(produto.get(), itemRequest.quantidade(), TipoMovimentacao.VENDA,
                    "Venda para " + usuario.get().getNome());
        }

        // 4) soma os subtotais dos itens: o total é sempre calculado aqui, nunca vem do front
        pedido.calcularTotal();

        // save() no pedido salva também os itens (cascade = ALL)
        // 5) grava o pedido (e os itens, por causa do cascade) e responde 201 com o pedido convertido em DTO
        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.de(salvo));
    }

    // GET /pedidos/usuario/{usuarioId} -> pedidos do usuário (mais recentes primeiro), ou 404
    // @Transactional(readOnly = true): só leitura. Mantém a conexão aberta enquanto os itens de cada pedido
    // são carregados (o Hibernate busca a lista de itens só quando alguém a usa).
    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        // 1) usuário inexistente: 404 (e não uma lista vazia, para o front saber que o id está errado)
        if (!usuarioRepository.existsById(usuarioId)) {
            return naoEncontrado("Usuário não encontrado.");
        }

        // 2) busca os pedidos (mais novos primeiro) e converte cada um em PedidoResponse
        List<PedidoResponse> pedidos = pedidoRepository.findByUsuarioIdOrderByDataDesc(usuarioId)
                .stream()
                .map(PedidoResponse::de)
                .toList();
        // 3) 200 com a lista (pode ser vazia, se o usuário ainda não comprou nada)
        return ResponseEntity.ok(pedidos);
    }

    // GET /pedidos/{id} -> 200 ou 404
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        // achou -> 200 com o pedido convertido; não achou -> 404 com mensagem.
        // <ResponseEntity<?>> antes do map só ajuda o Java a entender o tipo da resposta.
        return pedidoRepository.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(PedidoResponse.de(p)))
                .orElseGet(() -> naoEncontrado("Pedido não encontrado."));
    }

    // Monta a resposta 404 com {"mensagem": "..."}; usada em vários métodos desta classe.
    // Map.of("chave", "valor") cria um objeto simples que vira {"mensagem": "..."} no JSON.
    private ResponseEntity<Map<String, String>> naoEncontrado(String mensagem) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", mensagem));
    }
}
