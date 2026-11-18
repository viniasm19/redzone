package com.redzone.api.repositorio;

import com.redzone.api.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

// Acesso à tabela pedidos.
public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {
}
