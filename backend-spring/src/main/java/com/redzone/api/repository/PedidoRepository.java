package com.redzone.api.repository;

import com.redzone.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // O Spring monta a consulta pelo nome: pedidos do usuário, do mais novo para o mais antigo
    List<Pedido> findByUsuarioIdOrderByDataDesc(Long usuarioId);
}
