package com.redzone.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// @Entity: vira a tabela "movimentacoes_estoque". É o HISTÓRICO do estoque:
// toda vez que a quantidade de um produto muda, uma linha nova é gravada aqui.
// Assim dá para responder "por que esse produto tem 3 unidades?" olhando as movimentações.
@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitas movimentações -> um produto (coluna produto_id)
    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // EnumType.STRING grava o NOME ("ENTRADA") no banco, e não a posição (0, 1, 2).
    // Se um dia alguém mudar a ordem do enum, os dados antigos continuam certos.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentacao tipo;

    // Sempre positiva: o "tipo" é que diz se entrou ou saiu
    @Column(nullable = false)
    private Integer quantidade;

    // Quanto o produto ficou no estoque DEPOIS desta movimentação
    @Column(name = "estoque_apos", nullable = false)
    private Integer estoqueApos;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime data;

    @Column(length = 255)
    private String observacao;

    public MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(Produto produto, TipoMovimentacao tipo, Integer quantidade, String observacao) {
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.estoqueApos = produto.getEstoque();
        this.data = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Integer getEstoqueApos() {
        return estoqueApos;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getObservacao() {
        return observacao;
    }
}
