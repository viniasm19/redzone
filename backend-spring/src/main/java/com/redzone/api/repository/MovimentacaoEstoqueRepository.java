package com.redzone.api.repository;

import com.redzone.api.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acesso ao histórico de estoque.
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByProdutoIdOrderByDataDescIdDesc(Long produtoId);

    List<MovimentacaoEstoque> findAllByOrderByDataDescIdDesc();

    void deleteByProdutoId(Long produtoId);
}
