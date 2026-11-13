package com.redzone.api.repository;

import com.redzone.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Conversa com a tabela "pedidos".
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // O Spring monta a consulta pelo nome: pedidos do usuário.
    // findBy UsuarioId = WHERE usuario_id = ?.
    List<Pedido> findByUsuarioIdOrderByDataDesc(Long usuarioId);
}
