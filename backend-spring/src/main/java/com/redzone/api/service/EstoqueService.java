package com.redzone.api.service;

import com.redzone.api.model.MovimentacaoEstoque;
import com.redzone.api.model.Produto;
import com.redzone.api.model.TipoMovimentacao;
import com.redzone.api.repository.MovimentacaoEstoqueRepository;
import com.redzone.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Service: aqui ficam as REGRAS do estoque. Ficam num lugar só porque duas partes do sistema
// mexem no estoque: o administrador (EstoqueController) e a finalização da compra (PedidoController).
// Se cada controller tivesse sua própria regra, uma hora elas iam ficar diferentes.
@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // Soma unidades ao estoque (reposição)
    @Transactional
    public MovimentacaoEstoque entrada(Produto produto, int quantidade, String observacao) {
        validarQuantidade(quantidade);
        produto.setEstoque(produto.getEstoque() + quantidade);
        return registrar(produto, TipoMovimentacao.ENTRADA, quantidade, observacao);
    }

    // Tira unidades do estoque. Serve para SAIDA (ajuste/perda) e para VENDA.
    // Se não houver unidades suficientes, lança a exceção e NADA é alterado.
    @Transactional
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
        // A movimentação é criada DEPOIS de mudar o estoque, para gravar o "estoque após" certo
        return movimentacaoRepository.save(new MovimentacaoEstoque(produto, tipo, quantidade, observacao));
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
