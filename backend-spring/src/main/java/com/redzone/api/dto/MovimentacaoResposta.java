package com.redzone.api.dto;

import com.redzone.api.modelo.MovimentacaoEstoque;
import com.redzone.api.modelo.TipoMovimentacao;

import java.time.LocalDateTime;

// Movimentação que volta para o front.
public record MovimentacaoResposta(
        Long id,
        Long produtoId,
        String produtoNome,
        TipoMovimentacao tipo,
        Integer quantidade,
        Integer estoqueApos,
        LocalDateTime data,
        String observacao
) {
    public static MovimentacaoResposta de(MovimentacaoEstoque m) {
        return new MovimentacaoResposta(
                m.getId(),
                m.getProduto().getId(),
                m.getProduto().getNome(),
                m.getTipo(),
                m.getQuantidade(),
                m.getEstoqueApos(),
                m.getData(),
                m.getObservacao());
    }
}
