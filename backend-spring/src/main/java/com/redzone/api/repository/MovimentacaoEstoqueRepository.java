package com.redzone.api.repository;

import com.redzone.api.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    // O Spring Data lê o NOME do método e monta o SQL sozinho:
    // "find...By ProdutoId ... OrderBy Data Desc" = movimentações de um produto, mais recentes primeiro.
    List<MovimentacaoEstoque> findByProdutoIdOrderByDataDescIdDesc(Long produtoId);

    // Todas as movimentações, mais recentes primeiro
    List<MovimentacaoEstoque> findAllByOrderByDataDescIdDesc();

    // Usado ao excluir um produto: apaga o histórico dele junto
    void deleteByProdutoId(Long produtoId);
}
