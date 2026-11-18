package com.redzone.api.servico;

import com.redzone.api.modelo.MovimentacaoEstoque;
import com.redzone.api.modelo.Produto;
import com.redzone.api.modelo.TipoMovimentacao;
import com.redzone.api.repositorio.MovimentacaoEstoqueRepositorio;
import com.redzone.api.repositorio.ProdutoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Regras do estoque (usadas por pedidos e pelo admin).
@Service
public class EstoqueServico {

    private final ProdutoRepositorio produtoRepositorio;
    private final MovimentacaoEstoqueRepositorio movimentacaoRepositorio;

    public EstoqueServico(ProdutoRepositorio produtoRepositorio,
                          MovimentacaoEstoqueRepositorio movimentacaoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
        this.movimentacaoRepositorio = movimentacaoRepositorio;
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
            throw new EstoqueInsuficienteExcecao(
                    "Estoque insuficiente para \"" + produto.getNome() + "\": pedido de " + quantidade
                            + ", disponível " + produto.getEstoque() + ".");
        }

        produto.setEstoque(produto.getEstoque() - quantidade);
        return registrar(produto, tipo, quantidade, observacao);
    }

    private MovimentacaoEstoque registrar(Produto produto, TipoMovimentacao tipo, int quantidade, String observacao) {
        produtoRepositorio.save(produto);
        return movimentacaoRepositorio.save(new MovimentacaoEstoque(produto, tipo, quantidade, observacao));
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
