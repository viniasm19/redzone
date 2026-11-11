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
// @RestController: esta classe recebe requisições HTTP e devolve respostas em JSON.
//   (É um @Controller + @ResponseBody: o que os métodos retornam vira JSON automaticamente.)
// @RequestMapping("..."): o começo do endereço de todas as rotas desta classe.
@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    // Abaixo (ou igual) a isso o produto aparece como "estoque baixo"
    public static final int ESTOQUE_MINIMO = 5;

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final EstoqueService estoqueService;

    // Injeção de dependência: o Spring entrega os repositories e o EstoqueService prontos.
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
        // 1) sem produtoId: o histórico de todos; com produtoId: só daquele produto.
        //    "var" deixa o Java descobrir o tipo sozinho (aqui, uma List<MovimentacaoEstoque>).
        var lista = produtoId == null
                ? movimentacaoRepository.findAllByOrderByDataDescIdDesc()
                : movimentacaoRepository.findByProdutoIdOrderByDataDescIdDesc(produtoId);
        // 2) converte cada movimentação no DTO de resposta
        return lista.stream().map(MovimentacaoResponse::de).toList();
    }

    // POST /estoque/{produtoId}/entrada -> 201 com a movimentação, 400 ou 404
    @PostMapping("/{produtoId}/entrada")
    @Transactional
    public ResponseEntity<?> entrada(@PathVariable Long produtoId, @Valid @RequestBody MovimentacaoRequest request) {
        // 1) o produto precisa existir
        Optional<Produto> produto = produtoRepository.findById(produtoId);
        if (produto.isEmpty()) {
            return naoEncontrado();
        }

        // 2) quem soma e registra no histórico é o EstoqueService; depois, 201 com a movimentação
        var mov = estoqueService.entrada(produto.get(), request.quantidade(), request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResponse.de(mov));
    }

    // POST /estoque/{produtoId}/saida -> 201, 400, 404 ou 409 (não há unidades suficientes)
    @PostMapping("/{produtoId}/saida")
    @Transactional
    public ResponseEntity<?> saida(@PathVariable Long produtoId, @Valid @RequestBody MovimentacaoRequest request) {
        // 1) o produto precisa existir
        Optional<Produto> produto = produtoRepository.findById(produtoId);
        if (produto.isEmpty()) {
            return naoEncontrado();
        }

        // 2) o EstoqueService confere se há unidades suficientes (senão lança exceção -> 409) e registra
        var mov = estoqueService.saida(produto.get(), request.quantidade(), TipoMovimentacao.SAIDA, request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResponse.de(mov));
    }

    // Resposta 404 padrão desta classe
    private ResponseEntity<Map<String, String>> naoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Produto não encontrado."));
    }
}
