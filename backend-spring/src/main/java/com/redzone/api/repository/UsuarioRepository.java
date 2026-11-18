package com.redzone.api.repository;

import com.redzone.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Acesso à tabela usuarios.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

}
