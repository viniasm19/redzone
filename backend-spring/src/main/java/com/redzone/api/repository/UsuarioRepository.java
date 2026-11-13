package com.redzone.api.repository;

import com.redzone.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Além dos métodos prontos do JpaRepository.
// findByEmail -> SELECT ... WHERE email = ?
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // findByEmail: usado no reset de senha
    Optional<Usuario> findByEmail(String email);

    // IgnoreCase: "Lucas@Email.com" e "lucas@email.com" são o mesmo usuário.
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // true se existe alguém com este e-mail exato (não é usado hoje pelos controllers)
    boolean existsByEmail(String email);
}
