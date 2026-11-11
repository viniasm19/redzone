package com.redzone.api.dto;

import com.redzone.api.model.MovimentacaoEstoque;
import com.redzone.api.model.TipoMovimentacao;

import java.time.LocalDateTime;

// O que a API DEVOLVE de cada movimentação: só o necessário do produto (id e nome), não ele inteiro.
public record MovimentacaoResponse(
        Long id,
        Long produtoId,
        String produtoNome,
        TipoMovimentacao tipo,
        Integer quantidade,
        Integer estoqueApos,
        LocalDateTime data,
        String observacao
) {
    public static MovimentacaoResponse de(MovimentacaoEstoque m) {
        return new MovimentacaoResponse(
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
