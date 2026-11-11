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

// Controle de estoque em /estoque (usado pela tela de Estoque do administrador).
@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    // Abaixo (ou igual) a isso o produto aparece como "estoque baixo"
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

    // GET /estoque/baixo -> produtos com estoque baixo ou zerado (para alertas)
    @GetMapping("/baixo")
    public List<Produto> estoqueBaixo() {
        return produtoRepository.findByEstoqueLessThanEqualOrderByEstoqueAsc(ESTOQUE_MINIMO);
    }

    // GET /estoque/movimentacoes            -> histórico completo
    // GET /estoque/movimentacoes?produtoId=3 -> histórico de um produto
    @GetMapping("/movimentacoes")
    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> movimentacoes(@RequestParam(required = false) Long produtoId) {
        var lista = produtoId == null
                ? movimentacaoRepository.findAllByOrderByDataDescIdDesc()
                : movimentacaoRepository.findByProdutoIdOrderByDataDescIdDesc(produtoId);
        return lista.stream().map(MovimentacaoResponse::de).toList();
    }

    // POST /estoque/{produtoId}/entrada -> 201 com a movimentação, 400 ou 404
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

    // POST /estoque/{produtoId}/saida -> 201, 400, 404 ou 409 (não há unidades suficientes)
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
