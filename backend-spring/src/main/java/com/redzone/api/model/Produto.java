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

// =====================================================================
// Produto: um item à venda na loja.
// =====================================================================
// @Entity: esta classe vira a tabela "produtos" no banco; cada objeto Produto é uma linha.
// @Table(name = "produtos"): o nome da tabela.
// (As anotações @Id, @GeneratedValue, @Column e @NotBlank estão explicadas em detalhe no Usuario.java.)
@Entity
@Table(name = "produtos")
public class Produto {

    // @Id + @GeneratedValue: chave primária, gerada pelo banco (auto-incremento: 1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Regras de validação: o @Valid do controller as confere e, se falharem, a API responde 400.
    // @NotBlank: obrigatório e não pode ser só espaços. @Column(nullable = false): NOT NULL no banco.
    @NotBlank(message = "Informe o nome do produto.")
    @Column(nullable = false)
    private String nome;

    // @NotNull: obrigatório (para números usa-se @NotNull, porque @NotBlank é só para texto).
    // @Positive: precisa ser maior que zero (não existe produto de R$ 0,00 nem negativo).
    @NotNull(message = "Informe o preço.")
    @Positive(message = "O preço deve ser maior que zero.")
    @Column(nullable = false)
    private Double preco;

    // Caminho ou link da imagem (ex.: /img/produtos/headset.jpg).
    // @Column(length = 500): a coluna guarda até 500 caracteres (o padrão seria 255).
    @Column(length = 500)
    private String imagem;

    @NotBlank(message = "Informe a categoria.")
    @Column(nullable = false)
    private String categoria;

    // Descrição do produto (opcional: não tem @NotBlank)
    @Column(length = 500)
    private String descricao;

    // Quantas unidades existem no estoque.
    // @Min(0): não aceita número negativo.
    // @ColumnDefault("0"): quem já tinha o banco criado ganha a coluna nova com 0 nos produtos antigos.
    // Depois de cadastrado, o estoque só muda por movimentação (entrada, saída ou venda).
    @Min(value = 0, message = "O estoque não pode ser negativo.")
    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer estoque = 0;

    // ---------- Campos opcionais (detalhes do produto) ----------
    // Todos aceitam nulo: os produtos antigos (cadastrados antes destes campos existirem)
    // continuam válidos, e o ddl-auto=update só cria as colunas novas, sem apagar nada.

    // @Size(max = 100): no máximo 100 caracteres (a validação avisa antes de o banco recusar)
    @Size(max = 100, message = "A marca pode ter no máximo 100 caracteres.")
    @Column(length = 100)
    private String marca;

    // Texto livre, ex.: "12 meses"
    @Size(max = 100, message = "A garantia pode ter no máximo 100 caracteres.")
    @Column(length = 100)
    private String garantia;

    // Ficha técnica: uma especificação por linha (ex.: "Conexão: USB"). O front mostra cada linha como um item.
    @Size(max = 2000, message = "As especificações podem ter no máximo 2000 caracteres.")
    @Column(length = 2000)
    private String especificacoes;

    // Preço de promoção. Nulo = produto fora de promoção.
    // @Positive só é conferido quando o valor NÃO é nulo (o Bean Validation ignora campos nulos).
    @Positive(message = "O preço promocional deve ser maior que zero.")
    // @Column(name = ...): no Java o campo é "precoPromocional"; no banco a coluna é "preco_promocional"
    @Column(name = "preco_promocional")
    private Double precoPromocional;

    // Construtor vazio: o Hibernate e o Jackson precisam dele para criar o objeto
    public Produto() {
    }

    // Regra extra: uma promoção precisa ser MAIS BARATA que o preço normal.
    // @AssertTrue faz o @Valid chamar este método; se der false, a API responde 400.
    // @JsonIgnore: não vira um campo no JSON (é só uma conferência).
    @JsonIgnore
    @AssertTrue(message = "O preço promocional deve ser menor que o preço normal.")
    public boolean isPrecoPromocionalValido() {
        // Passo a passo: é válido se (1) não há promoção, ou (2) ainda não há preço para comparar
        // (quem reclama do preço vazio é o @NotNull), ou (3) a promoção é menor que o preço.
        return precoPromocional == null || preco == null || precoPromocional < preco;
    }

    // Quanto o produto custa AGORA: o preço promocional, se houver, senão o preço normal.
    // É este valor que o pedido cobra (sempre lido do banco, nunca do front).
    // @JsonIgnore: o front já recebe preco e precoPromocional e faz a mesma conta sozinho.
    @JsonIgnore
    public Double getPrecoAtual() {
        // "condição ? A : B" é um if/else numa linha: se tem promoção, devolve ela; senão, o preço normal
        return precoPromocional != null ? precoPromocional : preco;
    }

    // ---------- Getters e setters (leem e trocam os campos; o Jackson e o Hibernate usam) ----------

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
