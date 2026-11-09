package com.redzone.api.repository;

import com.redzone.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Além dos métodos prontos do JpaRepository, o Spring cria a consulta pelo NOME do método:
// findByEmail -> SELECT ... WHERE email = ?
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // findByEmail: usado no reset de senha
    Optional<Usuario> findByEmail(String email);

    // IgnoreCase: "Lucas@Email.com" e "lucas@email.com" são o mesmo usuário (usado no login)
    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);
}
