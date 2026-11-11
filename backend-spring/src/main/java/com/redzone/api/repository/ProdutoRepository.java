package com.redzone.api.repository;

import com.redzone.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository: a classe que conversa com a tabela "produtos" (buscar, salvar, apagar).
//
// É uma INTERFACE (só a "lista de métodos", sem o código dentro). Quem escreve o código é o Spring Data,
// sozinho, quando a aplicação sobe. JpaRepository<Produto, Long> = "repository de Produto, cujo id é Long".
//
// JpaRepository já traz pronto: save, findAll, findById, existsById, deleteById, count...
// Não precisa escrever SQL nem implementar nada.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Pelo NOME do método, o Spring monta a consulta: findBy + Estoque + LessThanEqual + OrderBy Estoque Asc =
    // SELECT * FROM produtos WHERE estoque <= ? ORDER BY estoque ASC   (usado na tela de Estoque)
    List<Produto> findByEstoqueLessThanEqualOrderByEstoqueAsc(Integer limite);

    // Produtos em promoção = os que têm preço promocional preenchido.
    // Pelo nome, o Spring monta: SELECT * FROM produtos WHERE preco_promocional IS NOT NULL
    List<Produto> findByPrecoPromocionalIsNotNull();
}
