package com.redzone.api.repository;

import com.redzone.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // findByEmail: usado no reset de senha
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
