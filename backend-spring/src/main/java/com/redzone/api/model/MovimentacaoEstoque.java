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

// Tabela movimentacoes_estoque: histórico do estoque.
@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Integer quantidade;

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
