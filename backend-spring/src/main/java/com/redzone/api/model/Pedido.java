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

// =====================================================================
// Pedido: uma compra feita por um usuário (com um ou mais itens).
// =====================================================================
// @Entity: vira a tabela "pedidos". Cada linha é uma compra.
// O pedido nasce AGUARDANDO_PAGAMENTO (PedidoController) e vira APROVADO no pagamento (PagamentoController).
@Entity
@Table(name = "pedidos")
public class Pedido {

    // Chave primária gerada pelo banco (auto-incremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne = "muitos para um": muitos pedidos podem ser de UM mesmo usuário.
    // optional = false: todo pedido TEM que ter um usuário.
    // @JoinColumn(name = "usuario_id"): o nome da coluna que guarda o id do usuário (a "chave estrangeira",
    // que aponta para uma linha de outra tabela).
    // Muitos pedidos -> um usuário. Cria a coluna usuario_id (chave estrangeira) na tabela.
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Quando o pedido foi feito. A coluna se chama data_pedido porque "data" é palavra reservada em alguns bancos.
    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime data;

    // Valor total do pedido, sempre calculado aqui no back-end (ver calcularTotal)
    @Column(nullable = false)
    private Double total;

    // @Enumerated(EnumType.STRING): diz como gravar um enum no banco (aqui, pelo nome).
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

    // @OneToMany = "um para muitos": UM pedido tem VÁRIOS itens.
    // orphanRemoval = true: se um item sair da lista, ele é apagado do banco.
    // Um pedido -> vários itens. mappedBy = "pedido": quem guarda a chave estrangeira é o ItemPedido.
    // cascade ALL: ao salvar o pedido, os itens são salvos junto (e apagados junto, se o pedido for apagado).
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    // Construtor vazio: exigido pelo Hibernate
    public Pedido() {
    }

    // Liga o item ao pedido dos DOIS lados (a lista do pedido e o campo "pedido" do item).
    // Recebe: o item novo. Não devolve nada.
    public void adicionarItem(ItemPedido item) {
        // 1) diz ao item a qual pedido ele pertence (é isso que preenche a coluna pedido_id)
        item.setPedido(this);
        // 2) coloca o item na lista do pedido (é isso que faz o cascade salvar o item junto)
        itens.add(item);
    }

    // Soma os subtotais dos itens. O total é sempre calculado aqui no back-end, nunca vem do front.
    // Não recebe nada; guarda o resultado no campo "total".
    public void calcularTotal() {
        // 1) percorre os itens (stream), pega o subtotal de cada um e soma tudo
        double soma = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
        // 2) arredonda para 2 casas: multiplica por 100, arredonda e divide por 100 (ex.: 439.80000001 -> 439.8)
        this.total = Math.round(soma * 100.0) / 100.0;
    }

    // ---------- Getters e setters ----------

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
        // se o banco tem um status, devolve ele; se está vazio (null), devolve APROVADO
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
