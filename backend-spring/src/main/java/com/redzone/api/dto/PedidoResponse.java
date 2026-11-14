package com.redzone.api.dto;

import com.redzone.api.model.FormaPagamento;
import com.redzone.api.model.Pedido;
import com.redzone.api.model.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

// Pedido que volta para o front.
public record PedidoResponse(
        Long id,
        Long usuarioId,
        LocalDateTime data,
        Double total,
        StatusPedido status,
        FormaPagamento formaPagamento,
        List<ItemResponse> itens
) {
    // Um item do pedido na resposta.
    public record ItemResponse(
            Long produtoId,
            String nome,
            String imagem,
            Integer quantidade,
            Double precoUnitario,
            Double subtotal
    ) {
    }

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
                pedido.getStatus(),
                pedido.getFormaPagamento(),
                itens);
    }
}
