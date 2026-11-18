package com.redzone.api.controlador;

import com.redzone.api.modelo.MovimentacaoEstoque;
import com.redzone.api.modelo.Produto;
import com.redzone.api.modelo.TipoMovimentacao;
import com.redzone.api.repositorio.MovimentacaoEstoqueRepositorio;
import com.redzone.api.repositorio.ProdutoRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Rotas de produtos: /produtos.
@RestController
@RequestMapping("/produtos")
public class ProdutoControlador {

    private final ProdutoRepositorio repositorio;
    private final MovimentacaoEstoqueRepositorio movimentacaoRepositorio;

    public ProdutoControlador(ProdutoRepositorio repositorio, MovimentacaoEstoqueRepositorio movimentacaoRepositorio) {
        this.repositorio = repositorio;
        this.movimentacaoRepositorio = movimentacaoRepositorio;
    }

    @GetMapping
    // Lista todos os produtos.
    public List<Produto> listar() {
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return repositorio.findById(id)
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

        Produto salvo = repositorio.save(produto);
        if (salvo.getEstoque() > 0) {
            movimentacaoRepositorio.save(new MovimentacaoEstoque(
                    salvo, TipoMovimentacao.ENTRADA, salvo.getEstoque(), "Estoque inicial"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    // Edita produto. Não muda o estoque.
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        return repositorio.findById(id)
                .map(atual -> {
                    produto.setId(id);
                    produto.setEstoque(atual.getEstoque());
                    return ResponseEntity.ok(repositorio.save(produto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    // Apaga produto e o histórico dele.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        movimentacaoRepositorio.deleteByProdutoId(id);
        repositorio.deleteById(id);
        repositorio.flush();
        return ResponseEntity.noContent().build();
    }
}
