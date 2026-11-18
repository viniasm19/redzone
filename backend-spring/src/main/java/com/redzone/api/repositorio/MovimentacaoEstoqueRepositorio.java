package com.redzone.api.repositorio;

import com.redzone.api.modelo.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acesso ao histórico de estoque.
public interface MovimentacaoEstoqueRepositorio extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByProdutoIdOrderByDataDescIdDesc(Long produtoId);

    List<MovimentacaoEstoque> findAllByOrderByDataDescIdDesc();

    void deleteByProdutoId(Long produtoId);
}
