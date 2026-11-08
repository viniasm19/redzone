package com.redzone.api.repository;

import com.redzone.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository já traz pronto: save, findAll, findById, existsById, deleteById...
// Não precisa escrever SQL nem implementar nada.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
