package com.redzone.api.repository;

import com.redzone.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Conversa com a tabela "pedidos". O Spring Data escreve o código sozinho (ver ProdutoRepository.java).
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // O Spring monta a consulta pelo nome: pedidos do usuário, do mais novo para o mais antigo.
    // findBy UsuarioId = WHERE usuario_id = ? ; OrderBy Data Desc = ORDER BY data_pedido DESC
    List<Pedido> findByUsuarioIdOrderByDataDesc(Long usuarioId);
}
