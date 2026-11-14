package com.redzone.api.repository;

import com.redzone.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acesso à tabela produtos (sem SQL escrito à mão).
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByEstoqueLessThanEqualOrderByEstoqueAsc(Integer limite);

    List<Produto> findByPrecoPromocionalIsNotNull();
}
