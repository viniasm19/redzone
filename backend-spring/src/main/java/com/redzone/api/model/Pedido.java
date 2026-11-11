package com.redzone.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Entity: vira a tabela "pedidos". Cada linha é uma compra finalizada.
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos pedidos -> um usuário. Cria a coluna usuario_id (chave estrangeira) na tabela.
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime data;

    @Column(nullable = false)
    private Double total;

    // Em que etapa o pedido está. EnumType.STRING grava o nome ("APROVADO"), não a posição (0, 1, 2...).
    // A coluna ACEITA nulo: pedidos feitos antes deste campo existir ficam com null no banco
    // (o ddl-auto=update só cria a coluna, sem mexer nas linhas). Quem trata o null é o getStatus().
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatusPedido status;

    // Como foi pago (CARTAO ou PIX). Nulo enquanto o pedido não foi pago, e nos pedidos antigos.
    // Nenhum dado do cartão é guardado: só a FORMA de pagamento.
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 20)
    private FormaPagamento formaPagamento;

    // Um pedido -> vários itens. mappedBy = "pedido": quem guarda a chave estrangeira é o ItemPedido.
    // cascade ALL: ao salvar o pedido, os itens são salvos junto (e apagados junto, se o pedido for apagado).
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    // Liga o item ao pedido dos DOIS lados (a lista do pedido e o campo "pedido" do item).
    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
    }

    // Soma os subtotais dos itens. O total é sempre calculado aqui no back-end, nunca vem do front.
    public void calcularTotal() {
        double soma = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
        this.total = Math.round(soma * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    // Pedido antigo (sem status gravado) aparece como APROVADO: antes desta mudança,
    // todo pedido salvo já era considerado uma compra concluída.
    public StatusPedido getStatus() {
        return status != null ? status : StatusPedido.APROVADO;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
}
