package com.redzone.api.repository;

import com.redzone.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository: a classe que conversa com a tabela "produtos" (buscar, salvar.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Pelo NOME do método, o Spring monta a consulta.
    // SELECT * FROM produtos WHERE estoque <= ? ORDER BY estoque ASC.
    List<Produto> findByEstoqueLessThanEqualOrderByEstoqueAsc(Integer limite);

    // Produtos em promoção = os que têm preço promocional preenchido.
    // Pelo nome, o Spring monta.
    List<Produto> findByPrecoPromocionalIsNotNull();
}
