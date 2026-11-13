package com.redzone.api.dto;

import com.redzone.api.model.FormaPagamento;
import com.redzone.api.model.Pedido;
import com.redzone.api.model.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

// O que a API DEVOLVE.
// que tem a senha, e evita o loop pedido -> itens -> pedido na conversão para.
public record PedidoResponse(
        Long id,
        Long usuarioId,
        LocalDateTime data,
        Double total,
        StatusPedido status,
        FormaPagamento formaPagamento, // null enquanto não foi pago
        List<ItemResponse> itens
) {
    // Como cada item aparece na resposta: nome e imagem do produto, quantidade.
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
    // Recebe: o pedido do banco. Devolve: o PedidoResponse pronto para virar JSON.
    public static PedidoResponse de(Pedido pedido) {
        // 1) converte cada ItemPedido (entidade) num ItemResponse (só os dados que o.
        // stream().map(...).toList() = "para cada item da lista.
        List<ItemResponse> itens = pedido.getItens().stream()
                .map(i -> new ItemResponse(
                        i.getProduto().getId(),
                        i.getProduto().getNome(),
                        i.getProduto().getImagem(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getSubtotal()))
                .toList();

        // 2) monta a resposta com os dados do pedido e a lista de itens convertida.
        // Do usuário vai só o id: nada de nome, e-mail ou senha.
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
