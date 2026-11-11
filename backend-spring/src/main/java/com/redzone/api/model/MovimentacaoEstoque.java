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

    // Chave primária gerada pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne + @JoinColumn: a coluna produto_id aponta para o produto movimentado.
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

    // Quando a movimentação aconteceu
    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime data;

    // Texto livre e opcional (ex.: "Reposição do fornecedor")
    @Column(length = 255)
    private String observacao;

    // Construtor vazio: exigido pelo Hibernate
    public MovimentacaoEstoque() {
    }

    // Construtor usado pelo sistema para registrar uma movimentação.
    // Recebe: o produto (JÁ com o estoque novo), o tipo, a quantidade e a observação.
    public MovimentacaoEstoque(Produto produto, TipoMovimentacao tipo, Integer quantidade, String observacao) {
        // 1) copia os dados recebidos para os campos
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.observacao = observacao;
        // 2) guarda quanto o produto ficou no estoque (por isso o produto já deve vir atualizado)
        this.estoqueApos = produto.getEstoque();
        // 3) marca a data e a hora de agora
        this.data = LocalDateTime.now();
    }

    // ---------- Getters (só leitura: uma movimentação não muda depois de gravada) ----------

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
