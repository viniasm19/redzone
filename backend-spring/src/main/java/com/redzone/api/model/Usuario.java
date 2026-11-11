package com.redzone.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// @Entity: esta classe vira a tabela "usuarios" no banco; cada objeto é uma linha.
@Entity
@Table(name = "usuarios")
public class Usuario {

    // Chave primária, gerada pelo banco (auto-incremento)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome.")
    @Column(nullable = false)
    private String nome;

    // O e-mail é único no banco de dados
    @NotBlank(message = "Informe o email.")
    @Email(message = "Email inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    // Senha em texto puro: aceitável só neste trabalho acadêmico (num sistema real, usar hash)
    // WRITE_ONLY: a API ACEITA a senha ao receber o JSON (cadastro, troca de senha),
    // mas NUNCA a devolve nas respostas (GET /usuarios, GET /usuarios/email/{email}, etc.).
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Informe a senha.")
    @Size(min = 4, message = "A senha deve ter pelo menos 4 caracteres.")
    @Column(nullable = false)
    private String senha;

    public Usuario() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
