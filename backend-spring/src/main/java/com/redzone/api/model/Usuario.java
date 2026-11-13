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

// Usuario: uma pessoa cadastrada na loja (cliente ou administrador).
// O que é uma "entidade"? É uma classe Java que o Hibernate.
@Entity
@Table(name = "usuarios")
public class Usuario {

    // @Id: este campo é a CHAVE PRIMÁRIA, o "RG" da linha (nunca se repete).
    // @GeneratedValue(IDENTITY): quem escolhe o número é o banco (1, 2, 3...).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotBlank: validação. Não aceita null, "" nem "   " (só espaços).
    // Quem confere é o @Valid do controller; se falhar.
    @NotBlank(message = "Informe o nome.")
    @Column(nullable = false)
    private String nome;

    // @Email: confere se o texto tem formato de e-mail (algo@algo).
    // unique = true: o banco não deixa existirem dois usuários com o mesmo e-mail.
    @NotBlank(message = "Informe o email.")
    @Email(message = "Email inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    // Senha em texto puro: aceitável só neste trabalho acadêmico (num sistema real.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Informe a senha.")
    @Size(min = 4, message = "A senha deve ter pelo menos 4 caracteres.")
    @Column(nullable = false)
    private String senha;

    // Construtor vazio: o Hibernate e o Jackson precisam dele para criar o objeto
    // e depois preencher os campos um por um.
    public Usuario() {
    }

    // ---------- Getters e setters ----------
    // "get" lê o valor de um campo; "set" troca o valor.

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
