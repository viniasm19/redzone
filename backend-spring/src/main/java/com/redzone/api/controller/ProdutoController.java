package com.redzone.api.controller;

import com.redzone.api.model.MovimentacaoEstoque;
import com.redzone.api.model.Produto;
import com.redzone.api.model.TipoMovimentacao;
import com.redzone.api.repository.MovimentacaoEstoqueRepository;
import com.redzone.api.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Rotas de produtos: /produtos.
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public ProdutoController(ProdutoRepository repository, MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.repository = repository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @GetMapping
    // Lista produtos. Com ?promocao=true, só os em promoção.
    public List<Produto> listar(@RequestParam(required = false) Boolean promocao) {
        if (Boolean.TRUE.equals(promocao)) {
            return repository.findByPrecoPromocionalIsNotNull();
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    // Cadastra produto (admin).
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        produto.setId(null);
        if (produto.getEstoque() == null) {
            produto.setEstoque(0);
        }

        Produto salvo = repository.save(produto);
        if (salvo.getEstoque() > 0) {
            movimentacaoRepository.save(new MovimentacaoEstoque(
                    salvo, TipoMovimentacao.ENTRADA, salvo.getEstoque(), "Estoque inicial"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    // Edita produto. Não muda o estoque.
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        return repository.findById(id)
                .map(atual -> {
                    produto.setId(id);
                    produto.setEstoque(atual.getEstoque());
                    return ResponseEntity.ok(repository.save(produto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    // Apaga produto e o histórico dele.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        movimentacaoRepository.deleteByProdutoId(id);
        repository.deleteById(id);
        repository.flush();
        return ResponseEntity.noContent().build();
    }
}
