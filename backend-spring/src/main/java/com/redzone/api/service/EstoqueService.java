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
// @Service: marca a classe como "serviço" (onde ficam as regras de negócio). O Spring cria o objeto
// sozinho e o entrega a quem pedir no construtor (injeção de dependência).
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
    // Recebe: o produto, quantas unidades entraram e uma observação. Devolve: a movimentação registrada.
    // @Transactional: mudar o estoque e gravar o histórico acontecem juntos (ou nenhum dos dois).
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
    // Recebe: o produto, a quantidade, o tipo (SAIDA ou VENDA) e a observação. Devolve: a movimentação registrada.
    public MovimentacaoEstoque saida(Produto produto, int quantidade, TipoMovimentacao tipo, String observacao) {
        // 1) a quantidade precisa ser maior que zero
        validarQuantidade(quantidade);

        // 2) não pode tirar mais do que existe: lança a exceção (que vira 409 no ApiExceptionHandler)
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
        // A movimentação é criada DEPOIS de mudar o estoque, para gravar o "estoque após" certo
        return movimentacaoRepository.save(new MovimentacaoEstoque(produto, tipo, quantidade, observacao));
    }

    // Lança IllegalArgumentException (vira 400 no ApiExceptionHandler) se a quantidade for zero ou negativa
    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
