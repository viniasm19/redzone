package com.redzone.api.service;

import com.redzone.api.model.MovimentacaoEstoque;
import com.redzone.api.model.Produto;
import com.redzone.api.model.TipoMovimentacao;
import com.redzone.api.repository.MovimentacaoEstoqueRepository;
import com.redzone.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Service: aqui ficam as REGRAS do estoque.
// mexem no estoque.
@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // Soma unidades ao estoque (reposição).
    // Recebe: o produto, quantas unidades entraram e uma observação. Devolve.
    @Transactional
    public MovimentacaoEstoque entrada(Produto produto, int quantidade, String observacao) {
        // 1) a quantidade precisa ser maior que zero (senão lança erro -> 400)
        validarQuantidade(quantidade);
        // 2) soma as unidades novas ao estoque atual
        produto.setEstoque(produto.getEstoque() + quantidade);
        // 3) salva o produto e grava a movimentação no histórico
        return registrar(produto, TipoMovimentacao.ENTRADA, quantidade, observacao);
    }

    // Tira unidades do estoque. Serve para SAIDA (ajuste/perda) e para VENDA.
    // Se não houver unidades suficientes, lança a exceção e NADA é alterado.
    @Transactional
    // Recebe: o produto, a quantidade, o tipo (SAIDA ou VENDA) e a observação.
    public MovimentacaoEstoque saida(Produto produto, int quantidade, TipoMovimentacao tipo, String observacao) {
        // 1) a quantidade precisa ser maior que zero
        validarQuantidade(quantidade);

        // 2) não pode tirar mais do que existe.
        if (produto.getEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para \"" + produto.getNome() + "\": pedido de " + quantidade
                            + ", disponível " + produto.getEstoque() + ".");
        }

        // 3) tira as unidades do estoque
        produto.setEstoque(produto.getEstoque() - quantidade);
        // 4) salva o produto e grava a movimentação no histórico
        return registrar(produto, tipo, quantidade, observacao);
    }

    // Usado pelos dois métodos acima. "private": só esta classe enxerga.
    private MovimentacaoEstoque registrar(Produto produto, TipoMovimentacao tipo, int quantidade, String observacao) {
        produtoRepository.save(produto);
        // A movimentação é criada DEPOIS de mudar o estoque.
        return movimentacaoRepository.save(new MovimentacaoEstoque(produto, tipo, quantidade, observacao));
    }

    // Lança IllegalArgumentException (vira 400 no ApiExceptionHandler) se a.
    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
