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

// CRUD de produtos em /produtos (o Angular chama estas rotas).
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    // O Spring entrega os repositories prontos (injeção de dependência).
    public ProdutoController(ProdutoRepository repository, MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.repository = repository;
        this.movimentacaoRepository = movimentacaoRepository;
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
    // O estoque inicial pode vir no cadastro; se vier, já entra no histórico como ENTRADA.
    @PostMapping
    @Transactional
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        produto.setId(null); // quem gera o id é o banco, mesmo que o front envie um
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

    // PUT /produtos/{id} -> 200 com o produto atualizado, ou 404
    // O estoque NÃO muda por aqui: ele é mantido como está no banco. Para mexer no estoque,
    // use /estoque/{id}/entrada ou /saida, que registram a movimentação no histórico.
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        return repository.findById(id)
                .map(atual -> {
                    produto.setId(id); // save() com um id que já existe faz UPDATE (sem id, faria INSERT)
                    produto.setEstoque(atual.getEstoque());
                    return ResponseEntity.ok(repository.save(produto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /produtos/{id} -> 204 (sem corpo), ou 404
    // O histórico de estoque do produto é apagado junto. Se o produto já tem pedidos, o banco barra
    // (409) e o @Transactional desfaz tudo, inclusive o histórico.
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        movimentacaoRepository.deleteByProdutoId(id);
        repository.deleteById(id);
        repository.flush(); // força o DELETE agora, para o erro de pedido (se houver) acontecer aqui dentro
        return ResponseEntity.noContent().build();
    }
}
