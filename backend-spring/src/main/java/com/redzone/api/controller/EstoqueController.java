package com.redzone.api.controller;

import com.redzone.api.dto.MovimentacaoRequest;
import com.redzone.api.dto.MovimentacaoResponse;
import com.redzone.api.model.Produto;
import com.redzone.api.model.TipoMovimentacao;
import com.redzone.api.repository.MovimentacaoEstoqueRepository;
import com.redzone.api.repository.ProdutoRepository;
import com.redzone.api.service.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Rotas do estoque (só o admin usa).
@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    // Abaixo disso o produto é 'estoque baixo'.
    public static final int ESTOQUE_MINIMO = 5;

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final EstoqueService estoqueService;

    public EstoqueController(ProdutoRepository produtoRepository,
                             MovimentacaoEstoqueRepository movimentacaoRepository,
                             EstoqueService estoqueService) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.estoqueService = estoqueService;
    }

    // Produtos com estoque baixo ou zerado.
    @GetMapping("/baixo")
    public List<Produto> estoqueBaixo() {
        return produtoRepository.findByEstoqueLessThanEqualOrderByEstoqueAsc(ESTOQUE_MINIMO);
    }

    // Histórico de entradas, saídas e vendas.
    @GetMapping("/movimentacoes")
    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> movimentacoes(@RequestParam(required = false) Long produtoId) {
        var lista = produtoId == null
                ? movimentacaoRepository.findAllByOrderByDataDescIdDesc()
                : movimentacaoRepository.findByProdutoIdOrderByDataDescIdDesc(produtoId);
        return lista.stream().map(MovimentacaoResponse::de).toList();
    }

    // Soma unidades ao estoque.
    @PostMapping("/{produtoId}/entrada")
    @Transactional
    public ResponseEntity<?> entrada(@PathVariable Long produtoId, @Valid @RequestBody MovimentacaoRequest request) {
        Optional<Produto> produto = produtoRepository.findById(produtoId);
        if (produto.isEmpty()) {
            return naoEncontrado();
        }

        var mov = estoqueService.entrada(produto.get(), request.quantidade(), request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResponse.de(mov));
    }

    // Tira unidades do estoque (409 se faltar).
    @PostMapping("/{produtoId}/saida")
    @Transactional
    public ResponseEntity<?> saida(@PathVariable Long produtoId, @Valid @RequestBody MovimentacaoRequest request) {
        Optional<Produto> produto = produtoRepository.findById(produtoId);
        if (produto.isEmpty()) {
            return naoEncontrado();
        }

        var mov = estoqueService.saida(produto.get(), request.quantidade(), TipoMovimentacao.SAIDA, request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResponse.de(mov));
    }

    private ResponseEntity<Map<String, String>> naoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Produto não encontrado."));
    }
}
