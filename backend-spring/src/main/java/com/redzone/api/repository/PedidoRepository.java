package com.redzone.api.repository;

import com.redzone.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acesso à tabela pedidos.
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioIdOrderByDataDesc(Long usuarioId);
}
