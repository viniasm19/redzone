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
// Os campos opcionais (marca, garantia, especificacoes.
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

    // GET /produtos               -> lista todos
    // GET /produtos?promocao=true -> só os produtos em promoção.
    @GetMapping
    // @RequestParam: lê um parâmetro do endereço, o que vem depois do "?" (ex..
    public List<Produto> listar(@RequestParam(required = false) Boolean promocao) {
        // 1) Boolean.TRUE.equals(...) dá true só se veio "true" (e não quebra se o.
        if (Boolean.TRUE.equals(promocao)) {
            return repository.findByPrecoPromocionalIsNotNull();
        }
        // 2) sem filtro: todos os produtos
        return repository.findAll();
    }

    // GET /produtos/{id} -> 200 com o produto, ou 404
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        // achou -> 200 com o produto; não achou -> 404 (Optional.
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /produtos -> 201 com o produto salvo.
    // @Valid confere as regras do model (nome, preço, categoria); se falhar.
    @PostMapping
    @Transactional
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        // 1) ignora um id vindo do front: o save() vai CRIAR um produto novo
        produto.setId(null); // quem gera o id é o banco, mesmo que o front envie um
        // 2) se o front não mandou o estoque, começa com 0
        if (produto.getEstoque() == null) {
            produto.setEstoque(0);
        }

        // 3) salva o produto (o banco gera o id)
        Produto salvo = repository.save(produto);
        // 4) se começou com unidades, registra essa entrada no histórico de estoque
        if (salvo.getEstoque() > 0) {
            movimentacaoRepository.save(new MovimentacaoEstoque(
                    salvo, TipoMovimentacao.ENTRADA, salvo.getEstoque(), "Estoque inicial"));
        }
        // 5) responde 201 (Created) com o produto salvo
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // PUT /produtos/{id} -> 200 com o produto atualizado, ou 404
    // O estoque NÃO muda por aqui: ele é mantido como está no banco.
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        // 1) procura o produto atual no banco ("atual"); se não existir.
        return repository.findById(id)
                .map(atual -> {
                    // 2) usa o id do endereço: o save() vai ALTERAR este produto
                    produto.setId(id); // save() com um id que já existe faz UPDATE (sem id, faria INSERT)
                    // 3) mantém o estoque que está no banco, ignorando o que o front mandou
                    produto.setEstoque(atual.getEstoque());
                    // 4) salva e responde 200 com o produto atualizado
                    return ResponseEntity.ok(repository.save(produto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /produtos/{id} -> 204 (sem corpo), ou 404
    // O histórico de estoque do produto é apagado junto. Se o produto já tem pedidos.
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // 1) não existe: 404
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // 2) apaga o histórico de estoque do produto (senão o banco não deixa apagar o.
        movimentacaoRepository.deleteByProdutoId(id);
        // 3) apaga o produto
        repository.deleteById(id);
        repository.flush(); // força o DELETE agora, para o erro de pedido (se houver) acontecer aqui dentro
        // 4) 204: apagado, sem corpo na resposta
        return ResponseEntity.noContent().build();
    }
}
