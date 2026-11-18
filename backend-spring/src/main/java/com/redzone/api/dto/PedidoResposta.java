package com.redzone.api.dto;

import com.redzone.api.modelo.FormaPagamento;
import com.redzone.api.modelo.Pedido;
import com.redzone.api.modelo.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

// Pedido que volta para o front.
public record PedidoResposta(
        Long id,
        Long usuarioId,
        LocalDateTime data,
        Double total,
        StatusPedido status,
        FormaPagamento formaPagamento,
        List<ItemResposta> itens
) {
    // Um item do pedido na resposta.
    public record ItemResposta(
            Long produtoId,
            String nome,
            String imagem,
            Integer quantidade,
            Double precoUnitario,
            Double subtotal
    ) {
    }

    public static PedidoResposta de(Pedido pedido) {
        List<ItemResposta> itens = pedido.getItens().stream()
                .map(i -> new ItemResposta(
                        i.getProduto().getId(),
                        i.getProduto().getNome(),
                        i.getProduto().getImagem(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubtotal()))
                .toList();

        return new PedidoResposta(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getData(),
                pedido.getTotal(),
                pedido.getStatus(),
                pedido.getFormaPagamento(),
                itens);
    }
}
