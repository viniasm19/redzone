package com.redzone.api.dto;

import com.redzone.api.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

// O que a API DEVOLVE. Usar um DTO (e não a entidade Pedido) evita expor o usuário inteiro,
// que tem a senha, e evita o loop pedido -> itens -> pedido na conversão para JSON.
public record PedidoResponse(
        Long id,
        Long usuarioId,
        LocalDateTime data,
        Double total,
        List<ItemResponse> itens
) {
    public record ItemResponse(
            Long produtoId,
            String nome,
            String imagem,
            Integer quantidade,
            Double precoUnitario,
            Double subtotal
    ) {
    }

    // Converte a entidade do banco no formato que vai para o front
    public static PedidoResponse de(Pedido pedido) {
        List<ItemResponse> itens = pedido.getItens().stream()
                .map(i -> new ItemResponse(
                        i.getProduto().getId(),
                        i.getProduto().getNome(),
                        i.getProduto().getImagem(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubtotal()))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getData(),
                pedido.getTotal(),
                itens);
    }
}
