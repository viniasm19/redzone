package com.redzone.api.repositorio;

import com.redzone.api.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

// Acesso à tabela produtos (sem SQL escrito à mão).
public interface ProdutoRepositorio extends JpaRepository<Produto, Long> {
}
