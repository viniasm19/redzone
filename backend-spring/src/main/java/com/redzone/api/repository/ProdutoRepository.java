package com.redzone.api.repository;

import com.redzone.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository já traz pronto: save, findAll, findById, existsById, deleteById...
// Não precisa escrever SQL nem implementar nada.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Pelo nome, o Spring monta: SELECT * FROM produtos WHERE estoque <= ? ORDER BY estoque ASC
    List<Produto> findByEstoqueLessThanEqualOrderByEstoqueAsc(Integer limite);

    // Produtos em promoção = os que têm preço promocional preenchido.
    // Pelo nome, o Spring monta: SELECT * FROM produtos WHERE preco_promocional IS NOT NULL
    List<Produto> findByPrecoPromocionalIsNotNull();
}
