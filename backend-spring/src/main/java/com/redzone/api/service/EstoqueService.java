package com.redzone.api.service;

import com.redzone.api.model.MovimentacaoEstoque;
import com.redzone.api.model.Produto;
import com.redzone.api.model.TipoMovimentacao;
import com.redzone.api.repository.MovimentacaoEstoqueRepository;
import com.redzone.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Regras do estoque (usadas por pedidos e pelo admin).
@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    // Soma unidades e registra no histórico.
    public MovimentacaoEstoque entrada(Produto produto, int quantidade, String observacao) {
        validarQuantidade(quantidade);
        produto.setEstoque(produto.getEstoque() + quantidade);
        return registrar(produto, TipoMovimentacao.ENTRADA, quantidade, observacao);
    }

    @Transactional
    // Tira unidades. Erro se não tiver o suficiente.
    public MovimentacaoEstoque saida(Produto produto, int quantidade, TipoMovimentacao tipo, String observacao) {
        validarQuantidade(quantidade);

        if (produto.getEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para \"" + produto.getNome() + "\": pedido de " + quantidade
                            + ", disponível " + produto.getEstoque() + ".");
        }

        produto.setEstoque(produto.getEstoque() - quantidade);
        return registrar(produto, tipo, quantidade, observacao);
    }

    private MovimentacaoEstoque registrar(Produto produto, TipoMovimentacao tipo, int quantidade, String observacao) {
        produtoRepository.save(produto);
        return movimentacaoRepository.save(new MovimentacaoEstoque(produto, tipo, quantidade, observacao));
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
