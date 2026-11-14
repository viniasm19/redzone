package com.redzone.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Tabela produtos: itens da loja.
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validações: nome, preço e categoria obrigatórios.
    @NotBlank(message = "Informe o nome do produto.")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Informe o preço.")
    @Positive(message = "O preço deve ser maior que zero.")
    @Column(nullable = false)
    private Double preco;

    @Column(length = 500)
    private String imagem;

    @NotBlank(message = "Informe a categoria.")
    @Column(nullable = false)
    private String categoria;

    @Column(length = 500)
    private String descricao;

    @Min(value = 0, message = "O estoque não pode ser negativo.")
    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer estoque = 0;


    // Campos opcionais (podem ser nulos).
    @Size(max = 100, message = "A marca pode ter no máximo 100 caracteres.")
    @Column(length = 100)
    private String marca;

    @Size(max = 100, message = "A garantia pode ter no máximo 100 caracteres.")
    @Column(length = 100)
    private String garantia;

    @Size(max = 2000, message = "As especificações podem ter no máximo 2000 caracteres.")
    @Column(length = 2000)
    private String especificacoes;

    // Preço promocional: nulo = sem promoção.
    @Positive(message = "O preço promocional deve ser maior que zero.")
    @Column(name = "preco_promocional")
    private Double precoPromocional;

    public Produto() {
    }

    @JsonIgnore
    @AssertTrue(message = "O preço promocional deve ser menor que o preço normal.")
    // Promoção tem que ser menor que o preço normal.
    public boolean isPrecoPromocionalValido() {
        return precoPromocional == null || preco == null || precoPromocional < preco;
    }

    @JsonIgnore
    // Preço que vale agora (promocional ou normal).
    public Double getPrecoAtual() {
        return precoPromocional != null ? precoPromocional : preco;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public String getEspecificacoes() {
        return especificacoes;
    }

    public void setEspecificacoes(String especificacoes) {
        this.especificacoes = especificacoes;
    }

    public Double getPrecoPromocional() {
        return precoPromocional;
    }

    public void setPrecoPromocional(Double precoPromocional) {
        this.precoPromocional = precoPromocional;
    }
}
