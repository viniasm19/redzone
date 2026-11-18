package com.redzone.api.controlador;

import com.redzone.api.dto.MovimentacaoRequisicao;
import com.redzone.api.dto.MovimentacaoResposta;
import com.redzone.api.modelo.Produto;
import com.redzone.api.modelo.TipoMovimentacao;
import com.redzone.api.repositorio.MovimentacaoEstoqueRepositorio;
import com.redzone.api.repositorio.ProdutoRepositorio;
import com.redzone.api.servico.EstoqueServico;
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
public class EstoqueControlador {

    private final ProdutoRepositorio produtoRepositorio;
    private final MovimentacaoEstoqueRepositorio movimentacaoRepositorio;
    private final EstoqueServico estoqueServico;

    public EstoqueControlador(ProdutoRepositorio produtoRepositorio,
                             MovimentacaoEstoqueRepositorio movimentacaoRepositorio,
                             EstoqueServico estoqueServico) {
        this.produtoRepositorio = produtoRepositorio;
        this.movimentacaoRepositorio = movimentacaoRepositorio;
        this.estoqueServico = estoqueServico;
    }

    // Histórico de entradas, saídas e vendas.
    @GetMapping("/movimentacoes")
    @Transactional(readOnly = true)
    public List<MovimentacaoResposta> movimentacoes(@RequestParam(required = false) Long produtoId) {
        var lista = produtoId == null
                ? movimentacaoRepositorio.findAllByOrderByDataDescIdDesc()
                : movimentacaoRepositorio.findByProdutoIdOrderByDataDescIdDesc(produtoId);
        return lista.stream().map(MovimentacaoResposta::de).toList();
    }

    // Soma unidades ao estoque.
    @PostMapping("/{produtoId}/entrada")
    @Transactional
    public ResponseEntity<?> entrada(@PathVariable Long produtoId, @Valid @RequestBody MovimentacaoRequisicao request) {
        Optional<Produto> produto = produtoRepositorio.findById(produtoId);
        if (produto.isEmpty()) {
            return naoEncontrado();
        }

        var mov = estoqueServico.entrada(produto.get(), request.quantidade(), request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResposta.de(mov));
    }

    // Tira unidades do estoque (409 se faltar).
    @PostMapping("/{produtoId}/saida")
    @Transactional
    public ResponseEntity<?> saida(@PathVariable Long produtoId, @Valid @RequestBody MovimentacaoRequisicao request) {
        Optional<Produto> produto = produtoRepositorio.findById(produtoId);
        if (produto.isEmpty()) {
            return naoEncontrado();
        }

        var mov = estoqueServico.saida(produto.get(), request.quantidade(), TipoMovimentacao.SAIDA, request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResposta.de(mov));
    }

    private ResponseEntity<Map<String, String>> naoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Produto não encontrado."));
    }
}
