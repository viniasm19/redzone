package com.redzone.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// ItemPedido: uma linha do pedido ("2x Headset Gamer a R$ 199,90").
// @Entity: vira a tabela "itens_pedido".
@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    // Chave primária gerada pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne + @JoinColumn.
    // Muitos itens -> um pedido (coluna pedido_id)
    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Muitos itens -> um produto (coluna produto_id)
    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Quantas unidades deste produto foram compradas
    @Column(nullable = false)
    private Integer quantidade;

    // Preço do produto NO MOMENTO da compra. Se o produto mudar de preço depois.
    @Column(name = "preco_unitario", nullable = false)
    private Double precoUnitario;

    // Construtor vazio: exigido pelo Hibernate
    public ItemPedido() {
    }

    // Subtotal do item = preço unitário x quantidade, arredondado para 2 casas.
    // Não é uma coluna do banco: é calculado na hora, sempre que alguém pede.
    public Double getSubtotal() {
        return Math.round(precoUnitario * quantidade * 100.0) / 100.0;
    }

    // ---------- Getters e setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
