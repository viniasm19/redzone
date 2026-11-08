package com.redzone.api.controller;

import com.redzone.api.model.Produto;
import com.redzone.api.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CRUD de produtos em /produtos (o Angular chama estas rotas).
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;

    // O Spring entrega o repository pronto (injeção de dependência).
    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    // GET /produtos -> lista todos
    @GetMapping
    public List<Produto> listar() {
        return repository.findAll();
    }

    // GET /produtos/{id} -> 200 com o produto, ou 404
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /produtos -> 201 com o produto salvo.
    // @Valid confere as regras do model (nome, preço, categoria); se falhar, a API responde 400.
    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        produto.setId(null); // quem gera o id é o banco, mesmo que o front envie um
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(produto));
    }

    // PUT /produtos/{id} -> 200 com o produto atualizado, ou 404
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        produto.setId(id); // save() com um id que já existe faz UPDATE (sem id, faria INSERT)
        return ResponseEntity.ok(repository.save(produto));
    }

    // DELETE /produtos/{id} -> 204 (sem corpo), ou 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
